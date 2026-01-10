package bookstore.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Book {

    private String isbn;
    private String title;
    private String author;
    private double price;
    private int stock;

    // Lock for advanced thread control
    private final Lock lock = new ReentrantLock();

    public Book(String isbn, String title, String author, double price, int stock) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    // ================== THREAD ==================

     

    // Restock with Lock
    public boolean restock(int quantity) {
        lock.lock();
        try {
            if (quantity > 0) {
                stock += quantity;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    // Thread-safe price update
    public synchronized void updatePrice(double newPrice) {
        if (newPrice > 0) {
            this.price = newPrice;
        }
    }

    // Check and reserve stock (atomic)
    public synchronized boolean reserveStock(int quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            System.out.println("[RESERVED] " + quantity +
                    " copies of '" + title + "' by " +
                    Thread.currentThread().getName());
            return true;
        }
        System.out.println("[FAILED] Not enough stock for '" +
                title + "' by " + Thread.currentThread().getName());
        return false;
    }

    // ================== STREAM ==================

    public static List<Book> findByAuthor(List<Book> books, String author) {
        return books.stream()
                .filter(b -> b.author.equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public static List<Book> findByPriceRange(List<Book> books,
                                              double minPrice,
                                              double maxPrice) {
        return books.stream()
                .filter(b -> b.price >= minPrice && b.price <= maxPrice)
                .sorted((b1, b2) -> Double.compare(b1.price, b2.price))
                .collect(Collectors.toList());
    }

    public static Map<String, List<Book>> groupByAuthor(List<Book> books) {
        return books.stream()
                .collect(Collectors.groupingBy(Book::getAuthor));
    }

    public static double averagePrice(List<Book> books) {
        return books.stream()
                .mapToDouble(Book::getPrice)
                .average()
                .orElse(0.0);
    }

    public static List<Book> findLowStock(List<Book> books, int threshold) {
        return books.stream()
                .filter(b -> b.stock <= threshold)
                .sorted((b1, b2) -> Integer.compare(b1.stock, b2.stock))
                .collect(Collectors.toList());
    }

    public static Map<String, Long> countByPriceCategory(List<Book> books) {
        return books.stream()
                .collect(Collectors.groupingBy(
                        b -> {
                            if (b.price < 20) return "Budget";
                            else if (b.price < 40) return "Standard";
                            else return "Premium";
                        },
                        Collectors.counting()
                ));
    }

    public static List<String> getAllAuthors(List<Book> books) {
        return books.stream()
                .map(Book::getAuthor)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // ================== GETTERS ==================

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    @Override
    public String toString() {
        return title + " | " + price + "€ | stock=" + stock;
    }
}
