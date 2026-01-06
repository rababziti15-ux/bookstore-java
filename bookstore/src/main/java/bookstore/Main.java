package bookstore;

import bookstore.model.Book;
import bookstore.thread.PurchaseTask;
import bookstore.thread.StockManager;
import bookstore.thread.StockManagerDBImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Main {
    public static void main(String[] args) {

        // 1️⃣ Création de l'implémentation du StockManager
        StockManager stockManager = new StockManagerDBImpl();

        // 2️⃣ On crée l'objet Book avec le bon ISBN (le stock réel est dans la DB)
        Book book = new Book("123456", "Java Programming", "Author A", 50, 0);

        // 3️⃣ Afficher le stock avant les achats
        System.out.println("Stock initial : " + getStock(book.getIsbn()));

        // 4️⃣ Création des threads clients
        Thread t1 = new Thread(new PurchaseTask(stockManager, book, 3, "Alice"));
        Thread t2 = new Thread(new PurchaseTask(stockManager, book, 4, "Bob"));
        Thread t3 = new Thread(new PurchaseTask(stockManager, book, 5, "Charlie"));
        Thread t4 = new Thread(new PurchaseTask(stockManager, book, 2, "Diana"));

        // 5️⃣ Lancement des threads
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        // 6️⃣ Attendre la fin de tous les threads
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 7️⃣ Afficher le stock final après achats
        System.out.println("Stock final : " + getStock(book.getIsbn()));
    }

    // Méthode utilitaire pour récupérer le stock directement depuis la DB
    private static int getStock(String isbn) {
        int stock = -1;
        try (Connection conn = config.DBConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT stock FROM books WHERE isbn = ?");
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stock;
    }
}
