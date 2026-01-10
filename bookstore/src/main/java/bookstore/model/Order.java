package bookstore.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Order {

    private static final AtomicInteger counter = new AtomicInteger(1); // Thread-safe counter

    private int id;
    private Customer customer;
    private List<OrderItem> items;
    private volatile String status; // Thread-safe status

    public Order(Customer customer) {
        this.id = counter.getAndIncrement(); // Thread-safe
        this.customer = customer;
        this.items = new ArrayList<>();
        this.status = "PENDING";
    }

    // ================== THREAD ==================
 
    // NEW: Update order status (thread-safe)
    public synchronized void updateStatus(String newStatus) {
        this.status = newStatus;
        System.out.println("[STATUS] Order #" + id + " updated to " + newStatus + " by " + Thread.currentThread().getName());
    }

    

    // NEW: Cancel order (thread-safe)
    public synchronized boolean cancelOrder() {
        if ("PENDING".equals(status)) {
            status = "CANCELLED";
            return true;
        }
        return false;
    }

    // ================== STREAM ==================
    // Total commande
    public double getTotal() {
        return items.stream()
                .mapToDouble(OrderItem::getSubTotal)
                .sum();
    }

    // Commande valide (tout le stock disponible)
    public boolean canBeProcessed() {
        return items.stream()
                .allMatch(item -> item.getBook().getStock() >= item.getQuantity());
    }

    // Livres distincts
    public long distinctBooksCount() {
        return items.stream()
                .map(i -> i.getBook().getIsbn())
                .distinct()
                .count();
    }

    // NEW: Total items quantity
    public int getTotalItemsQuantity() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    // NEW: Find most expensive item
    public OrderItem getMostExpensiveItem() {
        return items.stream()
                .max((i1, i2) -> Double.compare(i1.getSubTotal(), i2.getSubTotal()))
                .orElse(null);
    }

    // NEW: Filter items by minimum price
    public List<OrderItem> getItemsAbovePrice(double minPrice) {
        return items.stream()
                .filter(item -> item.getBook().getPrice() >= minPrice)
                .collect(Collectors.toList());
    }

    // NEW: Group items by author
    public Map<String, List<OrderItem>> groupItemsByAuthor() {
        return items.stream()
                .collect(Collectors.groupingBy(item -> item.getBook().getAuthor()));
    }

    // NEW: Find all orders with total above threshold
    public static List<Order> findLargeOrders(List<Order> orders, double threshold) {
        return orders.stream()
                .filter(o -> o.getTotal() >= threshold)
                .sorted((o1, o2) -> Double.compare(o2.getTotal(), o1.getTotal()))
                .collect(Collectors.toList());
    }

    // NEW: Calculate average order total
    public static double averageOrderTotal(List<Order> orders) {
        return orders.stream()
                         .mapToDouble(Order::getTotal)
                .average()
       .orElse(0.0);
    } 

    // NEW: Find orders by status
    public static List<Order> findOrdersByStatus(List<Order> orders, String status) {
        return orders.stream()
                .filter(o -> o.status.equals(status))
                .collect(Collectors.toList());
    }

    // NEW: Count orders by status
    public static Map<String, Long> countOrdersByStatus(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(o -> o.status, Collectors.counting()));
    }

    // NEW: Find orders with multiple items
    public static List<Order> findComplexOrders(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.items.size() > 1)
                .collect(Collectors.toList());
    }

    // NEW: Total revenue from all orders
    public static double totalRevenue(List<Order> orders) {
        return orders.stream()
                .filter(o -> "COMPLETED".equals(o.status))
                .mapToDouble(Order::getTotal)
                .sum();
    }

    public List<OrderItem> getItems() { return items; }
    public Customer getCustomer() { return customer; }
    public String getStatus() { return status; }
    public int getId() { return id; }

    @Override
    public String toString() {
        return "Order #" + id + " | total=" + getTotal() + "€ | status=" + status;
    }
}
