import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ties the four data structures together:
 *   MyHashTable  : product ID  -> Product       O(1) average lookup
 *   Trie         : name prefix -> product IDs   O(L + M)
 *   CategoryTree : category    -> product IDs   O(depth + k)
 *   PriceTree    : price range -> product IDs   O(log n + k)
 */
public class SearchEngine {

    private final MyHashTable<String, Product> products = new MyHashTable<>();
    private final Trie nameIndex = new Trie();
    private final CategoryTree categoryIndex = new CategoryTree();
    private final PriceTree priceIndex = new PriceTree();

    // ---------- add / update / remove ----------

    public void addProduct(Product p) {
        if (products.containsKey(p.getPid())) {
            removeProduct(p.getPid());             // replace the old version cleanly
        }
        products.put(p.getPid(), p);
        nameIndex.insertName(p.getName(), p.getPid());
        nameIndex.insertName(p.getBrand(), p.getPid());     // brand is searchable too
        categoryIndex.insert(p.getCategory(), p.getPid());
        priceIndex.insert(p.getPrice(), p.getPid());
    }

    // Changes a price and keeps the price tree in sync
    public boolean updatePrice(String pid, double newPrice) {
        Product p = products.get(pid);
        if (p == null) {
            return false;
        }
        priceIndex.remove(p.getPrice(), pid);
        p.setPrice(newPrice);                       // throws if newPrice < 0
        priceIndex.insert(newPrice, pid);
        return true;
    }

    // Removes the product from every index
    public boolean removeProduct(String pid) {
        Product p = products.get(pid);
        if (p == null) {
            return false;
        }
        nameIndex.removeName(p.getName(), pid);
        nameIndex.removeName(p.getBrand(), pid);
        categoryIndex.remove(p.getCategory(), pid);
        priceIndex.remove(p.getPrice(), pid);
        products.remove(pid);
        return true;
    }

    // ---------- searches ----------

    public Product getById(String pid) {
        return products.get(pid);
    }

    public List<Product> searchByName(String prefix) {
        return toProducts(nameIndex.searchPrefix(prefix));
    }

    public List<Product> searchByCategory(String categoryPath) {
        return toProducts(categoryIndex.productsUnder(categoryPath));
    }

    public List<Product> searchByPriceRange(double min, double max) {
        return toProducts(priceIndex.inRange(min, max));
    }

    // Combined search: name prefix AND price range AND minimum rating.
    // Pass null for namePrefix to skip the name filter.
    public List<Product> search(String namePrefix, double minPrice, double maxPrice, double minRating) {
        List<Product> result = new ArrayList<>();
        if (namePrefix == null || namePrefix.isBlank()) {
            for (Product p : searchByPriceRange(minPrice, maxPrice)) {
                if (p.getRating() >= minRating) {
                    result.add(p);
                }
            }
            return result;
        }
        Set<String> inPriceRange = new HashSet<>(priceIndex.inRange(minPrice, maxPrice));
        for (Product p : searchByName(namePrefix)) {
            if (inPriceRange.contains(p.getPid()) && p.getRating() >= minRating) {
                result.add(p);
            }
        }
        return result;
    }

    // Turns IDs into Products (skips removed products and duplicates)
    private List<Product> toProducts(List<String> ids) {
        List<Product> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (String id : ids) {
            Product p = products.get(id);
            if (p != null && seen.add(id)) {
                result.add(p);
            }
        }
        return result;
    }

    public int size() {
        return products.size();
    }

    public CategoryTree getCategoryTree() {
        return categoryIndex;
    }

    public PriceTree getPriceTree() {
        return priceIndex;
    }

    public MyHashTable<String, Product> getHashTable() {
        return products;
    }
}
