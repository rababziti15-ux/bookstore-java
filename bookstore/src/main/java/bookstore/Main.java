package main;

import service.BookService;
import service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class main {
    public static void main(String[] args) {

        // ==========================
        //  Afficher tous les livres
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

        // ==========================
        //  Créer un client fictif
        // ==========================
        Map<String, Object> customer = new HashMap<>();
        customer.put("name", "Nouhaila");
        customer.put("email", "nouhaila@example.com");

        // ==========================
        //  Créer une commande pour le client
        // ==========================
        Map<String, Object> order = OrderService.createOrder(customer);

        // ==========================
        // Ajouter des livres à la commande
        // ==========================
        System.out.println("\n===== Ajout des livres à la commande =====");
        OrderService.addBookToOrder(order, "123456", 2); // ISBN existant
        OrderService.addBookToOrder(order, "654321", 1); // un autre ISBN fictif

        // ==========================
        //  Valider la commande
        // ==========================
        if (!OrderService.validateOrder(order)) {
            System.out.println("Commande invalide !");
            return;
        }

        // ==========================
        //  Calculer le total
        // ==========================
        double total = OrderService.calculateOrderTotal(order);
        System.out.println("Total de la commande : " + total + " €");

        // ==========================
        //  Confirmer la commande
        // ==========================
        boolean confirmed = OrderService.confirmOrder(order);
        System.out.println(confirmed ? "Commande confirmée !" : "Échec de la confirmation");

        // ==========================
        //  Afficher les livres après achat
        // ==========================
        System.out.println("\n===== Liste des livres après achats =====");
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

        // ==========================
        //  Simulation achats concurrents
        // ==========================
        System.out.println("\n===== Simulation d'achats concurrents =====");
        OrderService.simulateConcurrentPurchases();

        try {
            Thread.sleep(2000); // attendre que les threads finissent
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
}
