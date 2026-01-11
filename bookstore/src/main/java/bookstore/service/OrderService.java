package service;

import java.sql.*;
import java.util.*;

public class OrderService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Nouha@2004";

    public static Map<String, Object> createOrder(Map<String, Object> customer) {
        Map<String, Object> order = new HashMap<>();
        order.put("customer", customer);
        order.put("items", new ArrayList<Map<String, Object>>());
        order.put("total", 0.0);
        return order;
    }

    @SuppressWarnings("unchecked")
    public static boolean addBookToOrder(Map<String, Object> order, String isbn, int quantity) {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String sql = "SELECT stock FROM books WHERE isbn = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();

            if (!rs.next() || rs.getInt("stock") < quantity) return false;

            Map<String, Object> item = new HashMap<>();
            item.put("isbn", isbn);
            item.put("quantity", quantity);
            item.put("price", BookService.getFinalPrice(isbn));

            ((List<Map<String, Object>>) order.get("items")).add(item);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public static double calculateOrderTotal(Map<String, Object> order) {
        double total = 0;
        for (Map<String, Object> item : (List<Map<String, Object>>) order.get("items")) {
            total += (double) item.get("price") * (int) item.get("quantity");
        }
        order.put("total", total);
        return total;
    }

    @SuppressWarnings("unchecked")
    public static boolean confirmOrder(Map<String, Object> order) {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            cn.setAutoCommit(false);

            for (Map<String, Object> item : (List<Map<String, Object>>) order.get("items")) {
                PreparedStatement ps = cn.prepareStatement(
                        "UPDATE books SET stock = stock - ? WHERE isbn = ? AND stock >= ?");
                int q = (int) item.get("quantity");
                ps.setInt(1, q);
                ps.setString(2, (String) item.get("isbn"));
                ps.setInt(3, q);

                if (ps.executeUpdate() == 0) {
                    cn.rollback();
                    return false;
                }
            }
            cn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
