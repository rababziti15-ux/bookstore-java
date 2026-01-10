package bookstore.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Customer {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private int id;
    private String name;
    private volatile double loyaltyPoints; // Thread-safe with volatile

    public Customer(String name) {
        this.id = COUNTER.getAndIncrement(); // THREAD SAFE
        this.name = name;
        this.loyaltyPoints = 0.0;
    }

    // ================== THREAD ==================
    // NEW: Add loyalty points (thread-safe)
    public synchronized void addLoyaltyPoints(double points) {
        loyaltyPoints += points;
        System.out.println("[LOYALTY] " + name + " earned " + points + " points. Total: " + loyaltyPoints);
    }

    // NEW: Redeem loyalty points (thread-safe)
    public synchronized boolean redeemPoints(double points) {
        if (loyaltyPoints >= points) {
            loyaltyPoints -= points;
            System.out.println("[REDEEM] " + name + " redeemed " + points + " points. Remaining: " + loyaltyPoints);
            return true;
        }
        return false;
    }

    // ================== STREAM ==================
    // Nombre total de commandes d'un client
    public long countOrders(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getCustomer().id == this.id)
                .count();
    }

    // NEW: Total spending for this customer
    public double totalSpending(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getCustomer().id == this.id)
                .mapToDouble(Order::getTotal)
                .sum();
    }

    // NEW: Find customer's most expensive order
    public Order mostExpensiveOrder(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getCustomer().id == this.id)
                .max((o1, o2) -> Double.compare(o1.getTotal(), o2.getTotal()))
                .orElse(null);
    }

    // NEW: Find all customers who spent more than threshold
    public static List<Customer> findBigSpenders(List<Customer> customers, List<Order> orders, double threshold) {
        return customers.stream()
                .filter(c -> {
                    double total = orders.stream()
                        .filter(o -> o.getCustomer().getId() == c.getId())
                        .mapToDouble(Order::getTotal)
                        .sum();
                    return total >= threshold;
                })
                .collect(Collectors.toList());
    }

    // NEW: Group customers by order count
    public static Map<String, List<Customer>> groupByOrderActivity(List<Customer> customers, List<Order> orders) {
        return customers.stream()
                .collect(Collectors.groupingBy(c -> {
                    long count = orders.stream()
                        .filter(o -> o.getCustomer().getId() == c.getId())
                        .count();
                    if (count == 0) return "Inactive";
                    else if (count <= 2) return "Regular";
                    else return "VIP";
                }));
    }

    // NEW: Find customers with no orders
    public static List<Customer> findInactiveCustomers(List<Customer> customers, List<Order> orders) {
        return customers.stream()
                .filter(c -> orders.stream()
                    .noneMatch(o -> o.getCustomer().getId() == c.getId()))
                .collect(Collectors.toList());
    }

    // NEW: Average order value per customer
    public static Map<Customer, Double> averageOrderValue(List<Customer> customers, List<Order> orders) {
        return customers.stream()
                .collect(Collectors.toMap(
                    c -> c,
                    c -> {
                        List<Order> customerOrders = orders.stream()
                            .filter(o -> o.getCustomer().getId() == c.getId())
                            .toList();
                        return customerOrders.isEmpty() ? 0.0 :
                            customerOrders.stream()
                                .mapToDouble(Order::getTotal)
                                .average()
                                .orElse(0.0);
                    }
                ));
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getLoyaltyPoints() { return loyaltyPoints; }

    @Override
    public String toString() {
        return "Customer #" + id + " " + name + " (Points: " + loyaltyPoints + ")";
    }
}
