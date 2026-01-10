package service;

import java.sql.*;

public class OrderService {

    private static final String URL = "jdbc:mysql://localhost:3306/bookstore";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Achat sécurisé (critique → synchronized)
    public static synchronized boolean buyBook(String isbn, int quantity) {

        try (Connection cn = DriverManager.getConnection(URL, USER, PASSWORD)) {

            // Vérification du stock
            String check = "SELECT stock FROM books WHERE isbn = ?";
            PreparedStatement psCheck = cn.prepareStatement(check);
            psCheck.setString(1, isbn);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) return false;

            int stock = rs.getInt("stock");

            if (stock < quantity) {
                System.out.println(Thread.currentThread().getName()
                        + " → Stock insuffisant");
                return false;
            }

            // Mise à jour
            String update = "UPDATE books SET stock = stock - ? WHERE isbn = ?";
            PreparedStatement psUpdate = cn.prepareStatement(update);
            psUpdate.setInt(1, quantity);
            psUpdate.setString(2, isbn);
            psUpdate.executeUpdate();

            System.out.println(Thread.currentThread().getName()
                    + " → Achat réussi");

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    //  Simulation de plusieurs clients
    public static void simulateConcurrentPurchases() {

        Runnable task = () -> buyBook("123456", 2);

        for (int i = 1; i <= 5; i++) {
            new Thread(task, "Client-" + i).start();
        }
    }
}
