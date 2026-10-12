import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TestSearch {
    private static int passed = 0;
    private static int total = 0;

    private static void check(String name, boolean condition) {
        total++;
        if (condition) passed++;
        System.out.println((condition ? "PASS - " : "FAIL - ") + name);
    }

    private static Set<String> ids(List<Product> list) {
        Set<String> s = new HashSet<>();
        for (Product p : list) s.add(p.getPid());
        return s;
    }

    public static void main(String[] args) {
        functionalTests();
        System.out.println();
        benchmark();
        System.out.println("\n" + passed + "/" + total + " tests passed");
    }

    // ------------------------------------------------------------------
    private static void functionalTests() {
        System.out.println("===== Functional tests =====");
        SearchEngine engine = new SearchEngine();
        engine.addProduct(new Product("P001", "Samsung Galaxy S21", "Electronics > Mobiles > Smartphones", "Samsung", 54999, 4.5));
        engine.addProduct(new Product("P002", "Apple iPhone 13", "Electronics > Mobiles > Smartphones", "Apple", 69999, 4.7));
        engine.addProduct(new Product("P003", "Dell Inspiron 15", "Electronics > Computers > Laptops", "Dell", 48500, 4.2));
        engine.addProduct(new Product("P004", "Sony WH-1000XM4", "Electronics > Audio > Headphones", "Sony", 24990, 4.6));
        engine.addProduct(new Product("P005", "boAt Rockerz 450", "Electronics > Audio > Headphones", "boAt", 1499, 4.1));
        engine.addProduct(new Product("P006", "Nike Air Runner", "Fashion > Men > Shoes", "Nike", 5495, 4.3));
        engine.addProduct(new Product("P007", "Prestige Mixer Grinder", "Home > Kitchen > Appliances", "Prestige", 3299, 4.0));
        engine.addProduct(new Product("P008", "Data Structures in Java", "Books > Education > Engineering", "Pearson", 650, 4.4));
        engine.addProduct(new Product("P009", "Samsung Galaxy Buds", "Electronics > Audio > Headphones", "Samsung", 1499, 4.0));

        // hash table: lookup by ID
        check("getById finds P003", engine.getById("P003") != null
                && engine.getById("P003").getName().equals("Dell Inspiron 15"));
        check("getById missing returns null", engine.getById("P999") == null);

        // trie: name prefix
        check("prefix 'gal' finds Galaxy S21 and Galaxy Buds",
                ids(engine.searchByName("gal")).equals(Set.of("P001", "P009")));
        check("prefix is case-insensitive ('IPH')",
                ids(engine.searchByName("IPH")).equals(Set.of("P002")));
        check("brand is searchable ('sam' -> 2 Samsung products)",
                ids(engine.searchByName("sam")).equals(Set.of("P001", "P009")));
        check("prefix with no match returns empty", engine.searchByName("xyz").isEmpty());
        check("blank prefix returns empty", engine.searchByName("  ").isEmpty());

        // category tree
        check("category 'Electronics > Audio' returns 3 products",
                ids(engine.searchByCategory("Electronics > Audio")).equals(Set.of("P004", "P005", "P009")));
        check("category 'Electronics' returns 6 products",
                engine.searchByCategory("Electronics").size() == 6);
        check("unknown category returns empty", engine.searchByCategory("Garden > Tools").isEmpty());
        check("sub-categories of Electronics",
                engine.getCategoryTree().subCategories("Electronics").equals(List.of("mobiles", "computers", "audio")));

        // price tree
        check("price range 1000-5000 finds P005, P009, P007",
                ids(engine.searchByPriceRange(1000, 5000)).equals(Set.of("P005", "P009", "P007")));
        check("two products share price 1499 (duplicate prices)",
                engine.searchByPriceRange(1499, 1499).size() == 2);
        check("price range with no products is empty", engine.searchByPriceRange(100000, 200000).isEmpty());

        // combined search
        List<Product> combo = engine.search("sam", 0, 10000, 4.0);
        check("combined: 'sam' + price <= 10000 + rating >= 4.0 -> Galaxy Buds only",
                ids(combo).equals(Set.of("P009")));
        check("combined with no name filter",
                ids(engine.search(null, 20000, 60000, 4.5)).equals(Set.of("P001", "P004")));

        // update price keeps the price tree in sync
        engine.updatePrice("P005", 999);
        check("after price update, old price no longer matches",
                !ids(engine.searchByPriceRange(1499, 1499)).contains("P005"));
        check("after price update, new price matches",
                ids(engine.searchByPriceRange(900, 1000)).equals(Set.of("P005")));
        check("updatePrice on missing product returns false", !engine.updatePrice("P999", 10));

        // remove
        check("removeProduct returns true", engine.removeProduct("P006"));
        check("removed product not found by ID", engine.getById("P006") == null);
        check("removed product not found by name", engine.searchByName("nike").isEmpty());
        check("removed product not found by category", engine.searchByCategory("Fashion").isEmpty());
        check("removed product not found by price", engine.searchByPriceRange(5495, 5495).isEmpty());
        check("size is 8 after removal", engine.size() == 8);

        // replacing a product with a new name must not leave the old name behind
        engine.addProduct(new Product("P001", "Samsung Galaxy S22", "Electronics > Mobiles > Smartphones", "Samsung", 59999, 4.6));
        check("replace keeps size at 8", engine.size() == 8);
        check("replaced product found by new name", ids(engine.searchByName("s22")).equals(Set.of("P001")));
        check("replaced product not found by old name", engine.searchByName("s21").isEmpty());

        // AVL balance: sorted inserts would make a plain BST a linked list
        PriceTree tree = new PriceTree();
        for (int i = 1; i <= 1023; i++) tree.insert(i, "X" + i);
        check("AVL height stays small for 1023 sorted inserts (height <= 11)", tree.height() <= 11);
        check("AVL performed rotations", tree.getRotations() > 0);
        check("AVL cheapest(3) returns the 3 lowest", tree.cheapest(3).equals(List.of("X1", "X2", "X3")));
    }

    // ------------------------------------------------------------------
    // Compare the data structures with a plain linear scan over a list
    private static void benchmark() {
        System.out.println("===== Benchmark: data structures vs linear scan =====");
        int[] sizes = {1_000, 10_000, 100_000};
        String[] words = {"phone", "laptop", "shoes", "watch", "camera", "tablet", "speaker", "router"};
        String[] cats = {"Electronics > Mobiles", "Electronics > Computers", "Fashion > Men", "Home > Kitchen"};
        Random rnd = new Random(42);

        System.out.printf("%-10s %-22s %-14s %-14s %-10s%n", "Products", "Operation", "Structure", "Linear scan", "Speed-up");
        for (int n : sizes) {
            SearchEngine engine = new SearchEngine();
            List<Product> list = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                String id = String.format("P%06d", i);
                String name = words[rnd.nextInt(words.length)] + " model" + rnd.nextInt(5000);
                Product p = new Product(id, name, cats[rnd.nextInt(cats.length)] + " > Type" + rnd.nextInt(10),
                        "Brand" + rnd.nextInt(50), 100 + rnd.nextInt(100000), 1 + rnd.nextInt(40) / 10.0);
                list.add(p);
                engine.addProduct(p);
            }

            int queries = 200;

            // 1. lookup by ID
            long t0 = System.nanoTime();
            int sink = 0;
            for (int q = 0; q < queries; q++) {
                if (engine.getById(String.format("P%06d", rnd.nextInt(n))) != null) sink++;
            }
            long hashTime = (System.nanoTime() - t0) / queries;
            t0 = System.nanoTime();
            for (int q = 0; q < queries; q++) {
                String target = String.format("P%06d", rnd.nextInt(n));
                for (Product p : list) {
                    if (p.getPid().equals(target)) { sink++; break; }
                }
            }
            long scanTime = (System.nanoTime() - t0) / queries;
            row(n, "Lookup by ID", "Hash table", hashTime, scanTime);

            // 2. name prefix
            t0 = System.nanoTime();
            for (int q = 0; q < queries; q++) {
                sink += engine.searchByName("model" + (1000 + rnd.nextInt(10))).size();
            }
            long trieTime = (System.nanoTime() - t0) / queries;
            t0 = System.nanoTime();
            for (int q = 0; q < queries; q++) {
                String prefix = "model" + (1000 + rnd.nextInt(10));
                for (Product p : list) {
                    for (String w : p.getName().toLowerCase().split(" ")) {
                        if (w.startsWith(prefix)) { sink++; break; }
                    }
                }
            }
            scanTime = (System.nanoTime() - t0) / queries;
            row(n, "Name prefix", "Trie", trieTime, scanTime);

            // 3. price range (narrow: about 0.05% of products)
            t0 = System.nanoTime();
            for (int q = 0; q < queries; q++) {
                double lo = 100 + rnd.nextInt(99000);
                sink += engine.searchByPriceRange(lo, lo + 50).size();
            }
            long avlTime = (System.nanoTime() - t0) / queries;
            t0 = System.nanoTime();
            for (int q = 0; q < queries; q++) {
                double lo = 100 + rnd.nextInt(99000);
                for (Product p : list) {
                    if (p.getPrice() >= lo && p.getPrice() <= lo + 50) sink++;
                }
            }
            scanTime = (System.nanoTime() - t0) / queries;
            row(n, "Price range", "AVL tree", avlTime, scanTime);

            check("benchmark ran at n=" + n, sink >= 0);
        }
        System.out.println("(times are average nanoseconds per query)");
    }

    private static void row(int n, String op, String structure, long structNs, long scanNs) {
        double speedup = structNs == 0 ? 0 : (double) scanNs / structNs;
        System.out.printf("%-10d %-22s %-14s %-14s %-10s%n", n, op, structNs + " ns", scanNs + " ns",
                String.format("%.0fx", speedup));
    }
}
