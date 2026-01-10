package service;

import java.sql.*;
import java.util.*;

public class OrderService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Nouha@2004";


    // =====================
    // Créer une commande pour un client
    // =====================
    public static Map<String, Object> createOrder(Map<String, Object> customer) {
        Map<String, Object> order = new HashMap<>();
        order.put("customer", customer);
        order.put("items", new ArrayList<Map<String, Object>>());
        order.put("total", 0.0);
        order.put("confirmed", false);
        return order;
    }

    // =====================
    // Ajouter un livre à la commande
    // =====================
    @SuppressWarnings("unchecked")
    public static boolean addBookToOrder(Map<String, Object> order, String isbn, int quantity) {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // Vérifier si le livre existe et stock disponible
            String sql = "SELECT * FROM books WHERE isbn = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Livre introuvable pour ISBN : " + isbn);
                return false;
            }

            int stock = rs.getInt("stock");
            if (stock < quantity) {
                System.out.println("Stock insuffisant pour ISBN : " + isbn);
                return false;
            }

            Map<String, Object> item = new HashMap<>();
            item.put("isbn", isbn);
            item.put("title", rs.getString("title"));
            item.put("quantity", quantity);
            item.put("price", rs.getDouble("price"));

            ((List<Map<String, Object>>) order.get("items")).add(item);

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =====================
    // Valider la commande (au moins un livre doit être présent)
    // =====================
    @SuppressWarnings("unchecked")
    public static boolean validateOrder(Map<String, Object> order) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        if (items.isEmpty()) {
            System.out.println("Commande invalide : aucun livre ajouté");
            return false;
        }
        return true;
    }

    // =====================
    // Calculer le total de la commande
    // =====================
    @SuppressWarnings("unchecked")
    public static double calculateOrderTotal(Map<String, Object> order) {
        double total = 0;
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        for (Map<String, Object> item : items) {
            total += (double) item.get("price") * (int) item.get("quantity");
        }
        order.put("total", total);
        return total;
    }

    // =====================
    // Confirmer la commande et mettre à jour le stock
    // =====================
    @SuppressWarnings("unchecked")
    public static boolean confirmOrder(Map<String, Object> order) {
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            cn.setAutoCommit(false); // transaction

            for (Map<String, Object> item : items) {
                String sql = "UPDATE books SET stock = stock - ? WHERE isbn = ? AND stock >= ?";
                PreparedStatement ps = cn.prepareStatement(sql);
                int quantity = (int) item.get("quantity");
                ps.setInt(1, quantity);
                ps.setString(2, (String) item.get("isbn"));
                ps.setInt(3, quantity);

                int updated = ps.executeUpdate();
                if (updated == 0) {
                    cn.rollback();
                    System.out.println("Échec de la commande : stock insuffisant pour " + item.get("isbn"));
                    return false;
                }
            }

            cn.commit();
            order.put("confirmed", true);
            System.out.println("Commande confirmée !");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =====================
    // Achat rapide (méthode existante)
    // =====================
    public static synchronized boolean buyBook(String isbn, int quantity) {
        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            String check = "SELECT stock FROM books WHERE isbn = ?";
            PreparedStatement psCheck = cn.prepareStatement(check);
            psCheck.setString(1, isbn);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) return false;

            int stock = rs.getInt("stock");

            if (stock < quantity) {
                System.out.println(Thread.currentThread().getName() + " → Stock insuffisant");
                return false;
            }

            String update = "UPDATE books SET stock = stock - ? WHERE isbn = ?";
            PreparedStatement psUpdate = cn.prepareStatement(update);
            psUpdate.setInt(1, quantity);
            psUpdate.setString(2, isbn);
            psUpdate.executeUpdate();

            System.out.println(Thread.currentThread().getName() + " → Achat réussi");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =====================
    // Simulation de plusieurs clients
    // =====================
    public static void simulateConcurrentPurchases() {
        Runnable task = () -> buyBook("123456", 2);

        for (int i = 1; i <= 5; i++) {
            new Thread(task, "Client-" + i).start();
        }
    }
}
