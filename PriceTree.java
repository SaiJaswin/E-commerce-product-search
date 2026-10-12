import java.util.ArrayList;
import java.util.List;

/**
 * Self-balancing AVL tree keyed by price.
 * Several products can have the same price, so every node holds a list of IDs.
 * Because the tree stays balanced, insert / delete / search are O(log n) and
 * a range query is O(log n + k), where k is the number of results.
 */
public class PriceTree {

    private static class Node {
        double price;
        List<String> productIds = new ArrayList<>();
        Node left, right;
        int height = 1;

        Node(double price, String pid) {
            this.price = price;
            this.productIds.add(pid);
        }
    }

    private Node root;
    private int rotations = 0;     // counted so the report can show balancing happens

    // ---------- AVL helpers ----------
    private int height(Node n) {
        return n == null ? 0 : n.height;
    }

    private void update(Node n) {
        n.height = 1 + Math.max(height(n.left), height(n.right));
    }

    private int balanceFactor(Node n) {
        return n == null ? 0 : height(n.left) - height(n.right);
    }

    private Node rotateRight(Node y) {
        Node x = y.left;
        y.left = x.right;
        x.right = y;
        update(y);
        update(x);
        rotations++;
        return x;
    }

    private Node rotateLeft(Node x) {
        Node y = x.right;
        x.right = y.left;
        y.left = x;
        update(x);
        update(y);
        rotations++;
        return y;
    }

    private Node rebalance(Node n) {
        update(n);
        int bf = balanceFactor(n);
        if (bf > 1) {                                   // left heavy
            if (balanceFactor(n.left) < 0) {
                n.left = rotateLeft(n.left);            // left-right case
            }
            return rotateRight(n);
        }
        if (bf < -1) {                                  // right heavy
            if (balanceFactor(n.right) > 0) {
                n.right = rotateRight(n.right);         // right-left case
            }
            return rotateLeft(n);
        }
        return n;
    }

    // ---------- insert ----------
    public void insert(double price, String pid) {
        root = insert(root, price, pid);
    }

    private Node insert(Node node, double price, String pid) {
        if (node == null) {
            return new Node(price, pid);
        }
        if (price < node.price) {
            node.left = insert(node.left, price, pid);
        } else if (price > node.price) {
            node.right = insert(node.right, price, pid);
        } else {
            node.productIds.add(pid);                   // same price: share the node
            return node;
        }
        return rebalance(node);
    }

    // ---------- remove (needed when a product's price changes) ----------
    public void remove(double price, String pid) {
        root = remove(root, price, pid);
    }

    private Node remove(Node node, double price, String pid) {
        if (node == null) {
            return null;
        }
        if (price < node.price) {
            node.left = remove(node.left, price, pid);
        } else if (price > node.price) {
            node.right = remove(node.right, price, pid);
        } else {
            node.productIds.remove(pid);
            if (!node.productIds.isEmpty()) {
                return node;                            // other products still use this price
            }
            // node is now empty: delete it from the tree
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node successor = node.right;                // smallest node in the right subtree
            while (successor.left != null) {
                successor = successor.left;
            }
            node.price = successor.price;
            node.productIds = new ArrayList<>(successor.productIds);
            // remove the successor's old node (delete all of its IDs from the right subtree)
            node.right = removeNode(node.right, successor.price);
        }
        return rebalance(node);
    }

    // Deletes the node with exactly this price, whatever IDs it holds
    private Node removeNode(Node node, double price) {
        if (node == null) {
            return null;
        }
        if (price < node.price) {
            node.left = removeNode(node.left, price);
        } else if (price > node.price) {
            node.right = removeNode(node.right, price);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }
            Node successor = node.right;
            while (successor.left != null) {
                successor = successor.left;
            }
            node.price = successor.price;
            node.productIds = new ArrayList<>(successor.productIds);
            node.right = removeNode(node.right, successor.price);
        }
        return rebalance(node);
    }

    // ---------- queries ----------

    // IDs of all products with min <= price <= max, in increasing price order
    public List<String> inRange(double min, double max) {
        List<String> result = new ArrayList<>();
        inRange(root, min, max, result);
        return result;
    }

    private void inRange(Node node, double min, double max, List<String> result) {
        if (node == null) {
            return;
        }
        if (min < node.price) {
            inRange(node.left, min, max, result);       // left side may still hold matches
        }
        if (min <= node.price && node.price <= max) {
            result.addAll(node.productIds);
        }
        if (max > node.price) {
            inRange(node.right, min, max, result);      // right side may still hold matches
        }
    }

    // IDs of the cheapest n products
    public List<String> cheapest(int n) {
        List<String> result = new ArrayList<>();
        cheapest(root, n, result);
        return result;
    }

    private void cheapest(Node node, int n, List<String> result) {
        if (node == null || result.size() >= n) {
            return;
        }
        cheapest(node.left, n, result);
        for (String id : node.productIds) {
            if (result.size() >= n) {
                return;
            }
            result.add(id);
        }
        cheapest(node.right, n, result);
    }

    public int height() {
        return height(root);
    }

    public int getRotations() {
        return rotations;
    }
}
