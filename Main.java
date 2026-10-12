import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Product> products = new ArrayList<>();

        products.add(new Product("P001", "Samsung Galaxy S21", "Electronics > Mobiles > Smartphones", "Samsung", 54999, 4.5));
        products.add(new Product("P002", "Apple iPhone 13", "Electronics > Mobiles > Smartphones", "Apple", 69999, 4.7));
        products.add(new Product("P003", "Dell Inspiron 15", "Electronics > Computers > Laptops", "Dell", 48500, 4.2));
        products.add(new Product("P004", "Sony WH-1000XM4", "Electronics > Audio > Headphones", "Sony", 24990, 4.6));
        products.add(new Product("P005", "boAt Rockerz 450", "Electronics > Audio > Headphones", "boAt", 1499, 4.1));
        products.add(new Product("P006", "Nike Air Runner", "Fashion > Men > Shoes", "Nike", 5495, 4.3));
        products.add(new Product("P007", "Prestige Mixer Grinder", "Home > Kitchen > Appliances", "Prestige", 3299, 4.0));
        products.add(new Product("P008", "Data Structures in Java", "Books > Education > Engineering", "Pearson", 650, 4.4));

        System.out.println("===== Product Catalog (" + products.size() + " items) =====");
        for (Product p : products) {
            System.out.println(p);
        }

        System.out.println("\nUpdating price of P005 to 1299...");
        products.get(4).setPrice(1299);
        System.out.println(products.get(4));

        System.out.println("\n===== Validation tests =====");
        testInvalid("negative price", () ->
                new Product("P100", "Bad Item", "Test", "X", -10, 4.0));
        testInvalid("rating above 5", () ->
                new Product("P101", "Bad Item", "Test", "X", 100, 7.5));
        testInvalid("empty ID", () ->
                new Product("", "Bad Item", "Test", "X", 100, 4.0));
    }

    private static void testInvalid(String label, Runnable action) {
        try {
            action.run();
            System.out.println("FAIL - " + label + " was accepted");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS - " + label + " rejected: " + e.getMessage());
        }
    }
}