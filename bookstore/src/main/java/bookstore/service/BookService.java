package service;

import java.sql.*;
import java.util.*;

public class BookService {

    private static final String URL =
            "jdbc:mysql://localhost:3306/bookstore?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Nouha@2004";

    // =====================
    // Calculer le prix final (PROMOTIONS)
    // =====================
    public static double getFinalPrice(String isbn) {
        String sql = """
            SELECT b.price AS base_price, p.type, p.value
            FROM books b
            LEFT JOIN promotions p
              ON p.active = true
             AND CURDATE() BETWEEN p.start_date AND p.end_date
             AND (
                 p.type = 'PERCENTAGE'
                 OR (p.type = 'AUTHOR' AND p.author = b.author)
             )
            WHERE b.isbn = ?
            ORDER BY p.id DESC
            LIMIT 1
        """;

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return -1;

            double base = rs.getDouble("base_price");
            String type = rs.getString("type");
            double value = rs.getDouble("value");

            if (type == null) return base;

            if ("PERCENTAGE".equals(type)) {
                return base * (1 - value / 100);
            }

            if ("AUTHOR".equals(type)) {
                return Math.max(0, base - value);
            }

            return base;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // =====================
    // Tous les livres
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
                book.put("final_price", getFinalPrice(rs.getString("isbn")));
                book.put("stock", rs.getInt("stock"));
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return books;
    }
}
