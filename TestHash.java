public class TestHash {
    private static int passed = 0;
    private static int total = 0;

    private static void check(String name, boolean condition) {
        total++;
        if (condition) passed++;
        System.out.println((condition ? "PASS - " : "FAIL - ") + name);
    }

    public static void main(String[] args) {
        MyHashTable<String, Product> table = new MyHashTable<>();

        // Test 1: put and get
        Product p1 = new Product("P001", "Samsung Galaxy S21", "Electronics > Mobiles", "Samsung", 54999, 4.5);
        Product p2 = new Product("P002", "Apple iPhone 13", "Electronics > Mobiles", "Apple", 69999, 4.7);
        Product p3 = new Product("P003", "Dell Inspiron 15", "Electronics > Laptops", "Dell", 48500, 4.2);
        table.put("P001", p1);
        table.put("P002", p2);
        table.put("P003", p3);
        check("put 3 products and get them back",
                table.get("P001") == p1 && table.get("P002") == p2 && table.get("P003") == p3);
        check("size is 3", table.size() == 3);

        // Test 2: same key twice updates the value, size stays the same
        Product p1b = new Product("P001", "Samsung Galaxy S22", "Electronics > Mobiles", "Samsung", 59999, 4.6);
        table.put("P001", p1b);
        check("duplicate key updates value", table.get("P001") == p1b);
        check("duplicate key does not change size", table.size() == 3);

        // Test 3: missing key
        check("get missing key returns null", table.get("P999") == null);

        // Test 4: remove
        check("remove existing key returns true", table.remove("P002"));
        check("removed key is gone", table.get("P002") == null);
        check("size is 2 after remove", table.size() == 2);
        check("remove missing key returns false", !table.remove("P002"));

        // Test 5: 1000 products, tests resize
        MyHashTable<String, Product> big = new MyHashTable<>();
        for (int i = 1; i <= 1000; i++) {
            String id = String.format("P%06d", i);
            big.put(id, new Product(id, "Item " + i, "Test", "Brand", i * 10.0, 4.0));
        }
        boolean allFound = true;
        for (int i = 1; i <= 1000; i++) {
            String id = String.format("P%06d", i);
            Product p = big.get(id);
            if (p == null || !p.getPid().equals(id)) {
                allFound = false;
                break;
            }
        }
        check("1000 products inserted, all found after resizing", allFound);
        check("size is 1000", big.size() == 1000);
        check("capacity grew beyond 16", big.getCapacity() > 16);
        System.out.println("   capacity = " + big.getCapacity()
                + ", longest chain = " + big.longestChain());

        // Test 6: small table shows collisions
        MyHashTable<String, Product> small = new MyHashTable<>(4);
        small.put("A", p1);
        small.put("B", p2);
        small.put("C", p3);
        System.out.println("\nSmall table (capacity 4) chains:");
        small.printChainLengths();

        System.out.println("\n" + passed + "/" + total + " tests passed");
    }
}