import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Trie for prefix search on product names.
 * Every word of a product name is inserted; the node where a word ends
 * remembers which product IDs contain that word.
 *
 * Example: insertName("Samsung Galaxy S21", "P001")
 *          searchPrefix("gal")  ->  [P001]
 */
public class Trie {

    private static class Node {
        Map<Character, Node> children = new HashMap<>();
        List<String> productIds = new ArrayList<>();   // filled only where a word ends
        boolean isEndOfWord = false;
    }

    private final Node root = new Node();

    // Insert one word and link it to a product ID. Time: O(L), L = word length
    public void insert(String word, String pid) {
        if (word == null || word.isBlank() || pid == null) {
            return;
        }
        Node current = root;
        for (char ch : word.toLowerCase().toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new Node());
        }
        current.isEndOfWord = true;
        if (!current.productIds.contains(pid)) {
            current.productIds.add(pid);
        }
    }

    // Insert every word of a product name, e.g. "Samsung Galaxy S21" -> 3 words
    public void insertName(String name, String pid) {
        if (name == null) {
            return;
        }
        for (String word : name.trim().split("\\s+")) {
            insert(word, pid);
        }
    }

    // Unlink a product ID from one word (nodes are kept, only the ID is removed)
    public void remove(String word, String pid) {
        if (word == null || word.isBlank() || pid == null) {
            return;
        }
        Node current = root;
        for (char ch : word.toLowerCase().toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return;
            }
        }
        current.productIds.remove(pid);
        if (current.productIds.isEmpty()) {
            current.isEndOfWord = false;
        }
    }

    public void removeName(String name, String pid) {
        if (name == null) {
            return;
        }
        for (String word : name.trim().split("\\s+")) {
            remove(word, pid);
        }
    }

    // IDs of all products having a word that starts with the prefix.
    // Time: O(P + M), P = prefix length, M = nodes in the subtree below the prefix
    public List<String> searchPrefix(String prefix) {
        if (prefix == null || prefix.isBlank()) {
            return new ArrayList<>();
        }
        Node current = root;
        for (char ch : prefix.trim().toLowerCase().toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return new ArrayList<>();          // no word starts with this prefix
            }
        }
        // a product can match through two words, so use a set to remove duplicates
        Set<String> result = new LinkedHashSet<>();
        collect(current, result);
        return new ArrayList<>(result);
    }

    // DFS: gather product IDs from this node and everything below it
    private void collect(Node node, Set<String> result) {
        if (node.isEndOfWord) {
            result.addAll(node.productIds);
        }
        for (Node child : node.children.values()) {
            collect(child, result);
        }
    }
}
