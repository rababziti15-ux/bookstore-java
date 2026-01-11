package bookstore.service;

import java.sql.*;
import java.util.*;

public class OrderService {

 private static final String URL = "jdbc:mysql://localhost:3306/bookstore";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // ==========================
    // Créer commande (mémoire)
    // ==========================
    public static Map<String, Object> createOrder(Map<String, Object> customer) {
        Map<String, Object> order = new HashMap<>();
        order.put("customer", customer);
        order.put("items", new ArrayList<Map<String, Object>>());
        order.put("total", 0.0);
        return order;
    }

    // ==========================
    // Ajouter livre à commande
    // ==========================
    @SuppressWarnings("unchecked")
    public static boolean addBookToOrder(Map<String, Object> order, String isbn, int quantity) {

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String sql = "SELECT stock FROM books WHERE isbn = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();

            if (!rs.next() || rs.getInt("stock") < quantity)
                return false;

            Map<String, Object> item = new HashMap<>();
            item.put("isbn", isbn);
            item.put("quantity", quantity);
            item.put("price", BookService.getFinalPrice(isbn)); // promo OK

            ((List<Map<String, Object>>) order.get("items")).add(item);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ==========================
    // Calculer total
    // ==========================
    @SuppressWarnings("unchecked")
    public static double calculateOrderTotal(Map<String, Object> order) {

        double total = 0;
        for (Map<String, Object> item :
                (List<Map<String, Object>>) order.get("items")) {

            total += (double) item.get("price")
                    * (int) item.get("quantity");
        }
        order.put("total", total);
        return total;
    }

    // ==========================
    // CONFIRMER commande (DB)
    // ==========================
    @SuppressWarnings("unchecked")
    public static boolean confirmOrder(Map<String, Object> order) {

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            cn.setAutoCommit(false);

            // 1️⃣ créer order
            PreparedStatement psOrder = cn.prepareStatement(
                    "INSERT INTO orders(order_date, total) VALUES (NOW(), ?)",
                    Statement.RETURN_GENERATED_KEYS);

            psOrder.setDouble(1, (double) order.get("total"));
            psOrder.executeUpdate();

            ResultSet rs = psOrder.getGeneratedKeys();
            rs.next();
            int orderId = rs.getInt(1);

            // 2️⃣ items + stock
            for (Map<String, Object> item :
                    (List<Map<String, Object>>) order.get("items")) {

                int q = (int) item.get("quantity");
                String isbn = (String) item.get("isbn");
                double price = (double) item.get("price");

                // update stock (TON CODE)
                PreparedStatement psStock = cn.prepareStatement(
                        "UPDATE books SET stock = stock - ? " +
                                "WHERE isbn = ? AND stock >= ?");
                psStock.setInt(1, q);
                psStock.setString(2, isbn);
                psStock.setInt(3, q);

                if (psStock.executeUpdate() == 0) {
                    cn.rollback();
                    return false;
                }

                // insert order_item (AJOUT)
                PreparedStatement psItem = cn.prepareStatement(
                        "INSERT INTO order_items(order_id, isbn, quantity, price) " +
                                "VALUES (?, ?, ?, ?)");

                psItem.setInt(1, orderId);
                psItem.setString(2, isbn);
                psItem.setInt(3, q);
                psItem.setDouble(4, price);
                psItem.executeUpdate();
            }

            cn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
