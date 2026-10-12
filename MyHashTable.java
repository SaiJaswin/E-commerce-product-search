public class MyHashTable<K, V> {

    private static class Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final double MAX_LOAD_FACTOR = 0.75;

    private Entry<K, V>[] buckets;
    private int size;
    private int capacity;

    public MyHashTable() {
        this(16);
    }

    @SuppressWarnings("unchecked")
    public MyHashTable(int initialCapacity) {
        capacity = initialCapacity;
        buckets = (Entry<K, V>[]) new Entry[capacity];
        size = 0;
    }

    // Turns a key into a bucket number from 0 to capacity-1
    private int hash(K key) {
        return (key.hashCode() & 0x7fffffff) % capacity;
    }

    // Insert a new key, or update the value if the key already exists
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        int index = hash(key);

        // 1. If the key is already in the chain, just update its value
        Entry<K, V> current = buckets[index];
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }

        // 2. Otherwise add a new entry at the front of the chain
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = buckets[index];
        buckets[index] = newEntry;
        size++;

        // 3. Grow the table if it is getting too full
        if ((double) size / capacity > MAX_LOAD_FACTOR) {
            resize();
        }
    }

    // Returns the value for the key, or null if not found
    public V get(K key) {
        if (key == null) {
            return null;
        }
        Entry<K, V> current = buckets[hash(key)];
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    // Removes the key. Returns true if it was found and removed
    public boolean remove(K key) {
        if (key == null) {
            return false;
        }
        int index = hash(key);
        Entry<K, V> current = buckets[index];
        Entry<K, V> previous = null;

        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = current.next;   // removing the first entry
                } else {
                    previous.next = current.next;    // skip over the removed entry
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    // Doubles the capacity and re-inserts every entry
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldBuckets = buckets;
        capacity = capacity * 2;
        buckets = (Entry<K, V>[]) new Entry[capacity];

        for (Entry<K, V> head : oldBuckets) {
            Entry<K, V> current = head;
            while (current != null) {
                Entry<K, V> next = current.next;      // remember before relinking
                int newIndex = hash(current.key);     // hash changes: capacity changed
                current.next = buckets[newIndex];
                buckets[newIndex] = current;
                current = next;
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public int getCapacity() {
        return capacity;
    }

    // Length of the longest chain: shows how well the hash spreads keys
    public int longestChain() {
        int longest = 0;
        for (Entry<K, V> head : buckets) {
            int length = 0;
            for (Entry<K, V> cur = head; cur != null; cur = cur.next) {
                length++;
            }
            longest = Math.max(longest, length);
        }
        return longest;
    }

    // Prints each bucket and how many entries it holds (good for a report screenshot)
    public void printChainLengths() {
        for (int i = 0; i < capacity; i++) {
            int length = 0;
            for (Entry<K, V> cur = buckets[i]; cur != null; cur = cur.next) {
                length++;
            }
            System.out.println("Bucket " + i + ": " + "#".repeat(length) + " (" + length + ")");
        }
    }
}