package bookstore;

import bookstore.model.Book;
import bookstore.thread.PurchaseTask;
import bookstore.thread.StockManager;
import bookstore.thread.StockManagerDBImpl;
import service.BookService;
import service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainCombined {
    public static void main(String[] args) {

        // ==========================
        // 1️⃣ Partie BookService / OrderService
        // ==========================
        System.out.println("===== Liste des livres =====");
        List<Map<String, Object>> books = BookService.getAllBooks();
        for (Map<String, Object> book : books) {
            System.out.println(
                    "ISBN: " + book.get("isbn") +
                    " | Titre: " + book.get("title") +
                    " | Auteur: " + book.get("author") +
                    " | Prix: " + book.get("price") +
                    " | Stock: " + book.get("stock")
            );
        }

        // Créer un client et une commande
        Map<String, Object> customer = new HashMap<>();
        customer.put("name", "Nouhaila");
        customer.put("email", "nouhaila@example.com");
        Map<String, Object> order = OrderService.createOrder(customer);

        // Ajouter des livres à la commande
        System.out.println("\n===== Ajout des livres à la commande =====");
        OrderService.addBookToOrder(order, "123456", 2);
        OrderService.addBookToOrder(order, "654321", 1);

        // Valider et confirmer la commande
        if (OrderService.validateOrder(order)) {
            double total = OrderService.calculateOrderTotal(order);
            System.out.println("Total de la commande : " + total + " €");
            boolean confirmed = OrderService.confirmOrder(order);
            System.out.println(confirmed ? "Commande confirmée !" : "Échec de la confirmation");
        } else {
            System.out.println("Commande invalide !");
        }

        // ==========================
        // 2️⃣ Partie StockManager / threads
        // ==========================
        StockManager stockManager = new StockManagerDBImpl();
        Book threadedBook = new Book("123456", "Java Programming", "Author A", 50, 0);

        System.out.println("Stock avant threads : " + getStock(threadedBook.getIsbn()));

        Thread t1 = new Thread(new PurchaseTask(stockManager, threadedBook, 3, "Alice"));
        Thread t2 = new Thread(new PurchaseTask(stockManager, threadedBook, 4, "Bob"));
        Thread t3 = new Thread(new PurchaseTask(stockManager, threadedBook, 5, "Charlie"));
        Thread t4 = new Thread(new PurchaseTask(stockManager, threadedBook, 2, "Diana"));

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Stock après threads : " + getStock(threadedBook.getIsbn()));

        // ==========================
        // 3️⃣ Afficher la liste finale des livres
        // ==========================
        System.out.println("\n===== Liste finale des livres =====");
        books = BookService.getAllBooks();
        for (Map<String, Object> book : books) {
            System.out.println(
                    "ISBN: " + book.get("isbn") +
                    " | Titre: " + book.get("title") +
                    " | Auteur: " + book.get("author") +
                    " | Prix: " + book.get("price") +
                    " | Stock: " + book.get("stock")
            );
        }
    }

    // Méthode utilitaire pour récupérer le stock directement depuis la DB
    private static int getStock(String isbn) {
        int stock = -1;
        try (java.sql.Connection conn = config.DBConnection.getConnection()) {
            java.sql.PreparedStatement ps = conn.prepareStatement("SELECT stock FROM books WHERE isbn = ?");
            ps.setString(1, isbn);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stock;
    }
}
