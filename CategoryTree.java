import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * General (n-ary) tree for product categories.
 * "Electronics > Mobiles > Smartphones" becomes the path
 * root -> Electronics -> Mobiles -> Smartphones, and the product ID is stored
 * at the last node. Asking for "Electronics > Mobiles" returns every product
 * in that node and in all nodes below it.
 */
public class CategoryTree {

    private static class Node {
        String name;
        Map<String, Node> children = new LinkedHashMap<>();
        List<String> productIds = new ArrayList<>();

        Node(String name) {
            this.name = name;
        }
    }

    private final Node root = new Node("ROOT");

    // Splits "A > B > C" into ["A", "B", "C"] and lowercases each part
    private String[] parsePath(String categoryPath) {
        String[] parts = categoryPath.split(">");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim().toLowerCase();
        }
        return parts;
    }

    // Add a product under its category path. Time: O(depth)
    public void insert(String categoryPath, String pid) {
        if (categoryPath == null || categoryPath.isBlank() || pid == null) {
            return;
        }
        Node current = root;
        for (String part : parsePath(categoryPath)) {
            if (part.isEmpty()) {
                continue;
            }
            current = current.children.computeIfAbsent(part, Node::new);
        }
        current.productIds.add(pid);
    }

    // Remove a product ID from its category node
    public void remove(String categoryPath, String pid) {
        if (categoryPath == null || categoryPath.isBlank() || pid == null) {
            return;
        }
        Node current = root;
        for (String part : parsePath(categoryPath)) {
            if (part.isEmpty()) {
                continue;
            }
            current = current.children.get(part);
            if (current == null) {
                return;
            }
        }
        current.productIds.remove(pid);
    }

    // All product IDs under the given category (including sub-categories)
    public List<String> productsUnder(String categoryPath) {
        List<String> result = new ArrayList<>();
        if (categoryPath == null || categoryPath.isBlank()) {
            return result;
        }
        Node current = root;
        for (String part : parsePath(categoryPath)) {
            if (part.isEmpty()) {
                continue;
            }
            current = current.children.get(part);
            if (current == null) {
                return result;                     // category does not exist
            }
        }
        collect(current, result);
        return result;
    }

    private void collect(Node node, List<String> result) {
        result.addAll(node.productIds);
        for (Node child : node.children.values()) {
            collect(child, result);
        }
    }

    // Sub-category names directly below a path (useful for a "browse" menu).
    // Pass "" or null for the top-level categories.
    public List<String> subCategories(String categoryPath) {
        List<String> names = new ArrayList<>();
        Node current = root;
        if (categoryPath != null && !categoryPath.isBlank()) {
            for (String part : parsePath(categoryPath)) {
                if (part.isEmpty()) {
                    continue;
                }
                current = current.children.get(part);
                if (current == null) {
                    return names;
                }
            }
        }
        for (Node child : current.children.values()) {
            names.add(child.name);
        }
        return names;
    }

    // Prints the whole tree with indentation (good for a report screenshot)
    public void print() {
        for (Node child : root.children.values()) {
            print(child, 0);
        }
    }

    private void print(Node node, int depth) {
        System.out.println("  ".repeat(depth) + node.name + " (" + node.productIds.size() + " direct)");
        for (Node child : node.children.values()) {
            print(child, depth + 1);
        }
    }
}
