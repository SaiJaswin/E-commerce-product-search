public class Product {
    private final String pid;      // final: an ID should never change
    private String name;
    private String category;       // e.g. "Electronics > Mobiles > Smartphones"
    private String brand;
    private double price;
    private double rating;

    public Product(String pid, String name, String category,
                   String brand, double price, double rating) {
        if (pid == null || pid.isBlank()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + price);
        }
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5: " + rating);
        }
        this.pid = pid;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.rating = rating;
    }

    // ---------- getters ----------
    public String getPid()      { return pid; }
    public String getName()     { return name; }
    public String getCategory() { return category; }
    public String getBrand()    { return brand; }
    public double getPrice()    { return price; }
    public double getRating()   { return rating; }

   
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative: " + price);
        }
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%-8s %-30s Rs.%,10.2f  %.1f*  %s",
                pid, name, price, rating, category);
    }
}
