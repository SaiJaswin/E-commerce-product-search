import java.util.List;

public class SearchDemo {
    public static void main(String[] args) {
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

        System.out.println("===== Category tree =====");
        engine.getCategoryTree().print();

        show("1. Lookup by ID: P004", engine.getById("P004") == null ? List.of() : List.of(engine.getById("P004")));
        show("2. Name search: \"gal\"", engine.searchByName("gal"));
        show("3. Category: Electronics > Audio", engine.searchByCategory("Electronics > Audio"));
        show("4. Price range: 1000 to 6000", engine.searchByPriceRange(1000, 6000));
        show("5. Combined: \"sam\", price <= 60000, rating >= 4.5", engine.search("sam", 0, 60000, 4.5));

        System.out.println("\nUpdating price of P005 to 999 ...");
        engine.updatePrice("P005", 999);
        show("Price range: 900 to 1000", engine.searchByPriceRange(900, 1000));

        System.out.println("\nPrice tree height: " + engine.getPriceTree().height()
                + ", rotations so far: " + engine.getPriceTree().getRotations());
    }

    private static void show(String title, List<Product> results) {
        System.out.println("\n" + title + "  ->  " + results.size() + " result(s)");
        for (Product p : results) {
            System.out.println("   " + p);
        }
    }
}
