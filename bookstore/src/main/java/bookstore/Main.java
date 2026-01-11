package main;

import service.BookService;
import service.OrderService;

import java.util.*;

public class main {
    public static void main(String[] args) {

        System.out.println("===== LIVRES =====");
        List<Map<String, Object>> books = BookService.getAllBooks();
        for (Map<String, Object> b : books) {
            System.out.println(
                    b.get("title") +
                            " | Prix: " + b.get("price") +
                            " | Promo: " + b.get("final_price") +
                            " | Stock: " + b.get("stock")
            );
        }

        Map<String, Object> customer = new HashMap<>();
        customer.put("name", "Nouhaila");

        Map<String, Object> order = OrderService.createOrder(customer);

        OrderService.addBookToOrder(order, "123456", 2);
        OrderService.addBookToOrder(order, "123457", 1);

        double total = OrderService.calculateOrderTotal(order);
        System.out.println("\nTOTAL = " + total + " €");

        if (OrderService.confirmOrder(order)) {
            System.out.println("Commande confirmée !");
        }

        System.out.println("\n===== APRÈS ACHAT =====");
        BookService.getAllBooks().forEach(b ->
                System.out.println(
                        b.get("title") +
                                " | Promo: " + b.get("final_price") +
                                " | Stock: " + b.get("stock")
                )
        );
    }
}
