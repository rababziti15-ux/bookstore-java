package bookstore.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderItem {

    private Book book;
    private int quantity;
    private volatile boolean processed; // Thread-safe flag

    public OrderItem(Book book, int quantity) {
        this.book = book;
        this.quantity = quantity;
        this.processed = false;
    }

    // ================== THREAD ==================
     
    // NEW: Cancel processing (thread-safe)
    public synchronized boolean cancelProcess() {
        if (processed) {
            processed = false;
            book.restock(quantity); // Return to stock
            return true;
        }
        return false;
    }

    // NEW: Update quantity (thread-safe)
    public synchronized boolean updateQuantity(int newQuantity) {
        if (!processed && newQuantity > 0) {
            this.quantity = newQuantity;
            return true;
        }
        return false;
    }

    public double getSubTotal() {
        return book.getPrice() * quantity;
    }

    // ================== STREAM ==================
    // Quantité totale achetée pour un livre donné
    public static int totalQuantity(List<OrderItem> items, Book book) {
        return items.stream()
                .filter(i -> i.book.getIsbn().equals(book.getIsbn()))
                .mapToInt(i -> i.quantity)
                .sum();
    }

    // NEW: Find items for a specific book
    public static List<OrderItem> findItemsByBook(List<OrderItem> items, String isbn) {
        return items.stream()
                .filter(i -> i.book.getIsbn().equals(isbn))
                .collect(Collectors.toList());
    }

    // NEW: Total revenue from items
    public static double totalRevenue(List<OrderItem> items) {
        return items.stream()
                .filter(i -> i.processed)
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
    }

    // NEW: Find most popular book (by quantity sold)
    public static Book findMostPopularBook(List<OrderItem> items) {
        return items.stream()
                .collect(Collectors.groupingBy(
                    OrderItem::getBook,
                    Collectors.summingInt(OrderItem::getQuantity)
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // NEW: Group items by price range
    public static Map<String, List<OrderItem>> groupByPriceRange(List<OrderItem> items) {
        return items.stream()
                .collect(Collectors.groupingBy(i -> {
                    double price = i.book.getPrice();
                    if (price < 20) return "Under 20€";
                    else if (price < 40) return "20-40€";
                    else return "Over 40€";
                }));
    }

    // NEW: Find items with high value (subtotal)
    public static List<OrderItem> findHighValueItems(List<OrderItem> items, double threshold) {
        return items.stream()
                .filter(i -> i.getSubTotal() >= threshold)
                .sorted((i1, i2) -> Double.compare(i2.getSubTotal(), i1.getSubTotal()))
                .collect(Collectors.toList());
    }

    // NEW: Count processed vs unprocessed items
    public static Map<String, Long> countByProcessStatus(List<OrderItem> items) {
        return items.stream()
                .collect(Collectors.groupingBy(
                    i -> i.processed ? "Processed" : "Pending",
                    Collectors.counting()
                ));
    }

    // NEW: Average quantity per item
    public static double averageQuantity(List<OrderItem> items) {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .average()
                .orElse(0.0);
    }

    // NEW: Find all unique books in items
    public static List<Book> getUniqueBooks(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getBook)
                .distinct()
                .collect(Collectors.toList());
    }

    // NEW: Total items count
    public static long countTotalItems(List<OrderItem> items) {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public Book getBook() { return book; }
    public int getQuantity() { return quantity; }
    public boolean isProcessed() { return processed; }

    @Override
    public String toString() {
        return book.getTitle() + " x " + quantity + (processed ? " [PROCESSED]" : " [PENDING]");
    }
}
