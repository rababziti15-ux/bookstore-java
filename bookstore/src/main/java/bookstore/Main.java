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

            /* ================= Awfa's Main ================= */

        /* ================= BOOK STORE STATISTICS ================= */

        System.out.println("\n====================================");
        System.out.println("        BOOK STORE STATISTICS        ");
        System.out.println("====================================");

        /* ---------- BOOK STATISTICS ---------- */

        System.out.println("\n[ BOOKS ]");

        System.out.println("• Total books        : "
                + statisticsService.countBooks(books));

        statisticsService.averageBookPrice(books)
                .ifPresent(avg ->
                        System.out.println("• Average price      : " + avg)
                );

        Book expensiveBook = statisticsService.mostExpensiveBook(books);
        if (expensiveBook != null) {
            System.out.println("• Most expensive     : "
                    + expensiveBook.getTitle());
        }

        System.out.println("• Total stock        : "
                + statisticsService.totalStock(books));

        /* ---------- ORDER / SALES STATISTICS ---------- */

        try (Connection cn = DBConnection.getConnection()) {

            System.out.println("\n[ ORDERS & SALES ]");

            System.out.println("• Orders count       : "
                    + statisticsService.countOrdersFromDB(cn));

            System.out.println("• Order items count  : "
                    + statisticsService.countOrderItemsFromDB(cn));

            System.out.println("• Books sold         : "
                    + statisticsService.totalBooksSoldFromDB(cn));

            System.out.println("• Total sales        : "
                    + statisticsService.totalSalesFromDB(cn));

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("====================================\n");
}
