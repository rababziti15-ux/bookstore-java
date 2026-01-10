package service;

import java.sql.*;
import java.util.*;

public class BookService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Nouha@2004";


    // =====================
    // Récupérer tous les livres
    // =====================
    public static List<Map<String, Object>> getAllBooks() {
        List<Map<String, Object>> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> book = new HashMap<>();
                book.put("isbn", rs.getString("isbn"));
                book.put("title", rs.getString("title"));
                book.put("author", rs.getString("author"));
                book.put("price", rs.getDouble("price"));
                book.put("stock", rs.getInt("stock"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // =====================
    // Récupérer les livres disponibles (stock > 0)
    // =====================
    public static List<Map<String, Object>> getAvailableBooks() {
        List<Map<String, Object>> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE stock > 0";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> book = new HashMap<>();
                book.put("isbn", rs.getString("isbn"));
                book.put("title", rs.getString("title"));
                book.put("author", rs.getString("author"));
                book.put("price", rs.getDouble("price"));
                book.put("stock", rs.getInt("stock"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // =====================
    // Récupérer les livres par auteur
    // =====================
    public static List<Map<String, Object>> getBooksByAuthor(String author) {
        List<Map<String, Object>> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE author = ?";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, author);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> book = new HashMap<>();
                book.put("isbn", rs.getString("isbn"));
                book.put("title", rs.getString("title"));
                book.put("author", rs.getString("author"));
                book.put("price", rs.getDouble("price"));
                book.put("stock", rs.getInt("stock"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // =====================
    // Récupérer les livres par prix
    // =====================
    public static List<Map<String, Object>> getBooksByPriceRange(double min, double max) {
        List<Map<String, Object>> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE price BETWEEN ? AND ?";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> book = new HashMap<>();
                book.put("isbn", rs.getString("isbn"));
                book.put("title", rs.getString("title"));
                book.put("author", rs.getString("author"));
                book.put("price", rs.getDouble("price"));
                book.put("stock", rs.getInt("stock"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }

    // =====================
    // Ajouter un nouveau livre
    // =====================
    public static boolean addBook(Map<String, Object> book) {
        String sql = "INSERT INTO books (isbn, title, author, price, stock) VALUES (?, ?, ?, ?, ?)";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, (String) book.get("isbn"));
            ps.setString(2, (String) book.get("title"));
            ps.setString(3, (String) book.get("author"));
            ps.setDouble(4, (Double) book.get("price"));
            ps.setInt(5, (Integer) book.get("stock"));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =====================
    // Mettre à jour le stock d’un livre
    // =====================
    public static boolean updateStock(String isbn, int quantity) {
        String sql = "UPDATE books SET stock = ? WHERE isbn = ?";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setString(2, isbn);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =====================
    // Supprimer un livre
    // =====================
    public static boolean removeBook(String isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, isbn);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // =====================
    // Calculer la valeur totale du stock
    // =====================
    public static double getTotalStockValue() {
        double total = 0;
        String sql = "SELECT SUM(price * stock) AS totalValue FROM books";

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                total = rs.getDouble("totalValue");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    // =====================
    // Affichage concurrent
    // =====================
    public static void showBooksConcurrently() {
        Runnable task = () -> {
            List<Map<String, Object>> books = getAllBooks();
            System.out.println("Utilisateur : " + Thread.currentThread().getName());

            books.forEach(b ->
                    System.out.println(
                            b.get("title") + " | Prix: " + b.get("price") + " | Stock: " + b.get("stock")
                    )
            );

            System.out.println("------------------------");
        };

        for (int i = 1; i <= 3; i++) {
            new Thread(task, "User-" + i).start();
        }
    }
}
