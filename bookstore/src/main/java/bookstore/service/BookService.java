package service;

import java.sql.*;
import java.util.*;

public class BookService {

    private static final String URL = "jdbc:mysql://localhost:3306/bookstore";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Récupérer tous les livres
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

    // Affichage concurrent (plusieurs utilisateurs)
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
