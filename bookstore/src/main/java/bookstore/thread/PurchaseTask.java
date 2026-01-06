package bookstore.thread;

import bookstore.model.Book;

public class PurchaseTask implements Runnable {

    private final StockManager stockManager;
    private final Book book;
    private final int quantity;
    private final String clientName;

    public PurchaseTask(
            StockManager stockManager,
            Book book,
            int quantity,
            String clientName) {

        this.stockManager = stockManager;
        this.book = book;
        this.quantity = quantity;
        this.clientName = clientName;
    }

    @Override
    public void run() {

        boolean success = stockManager.buyBook(book, quantity);

        if (success) {
            System.out.println(
                clientName + " ✅ achat réussi de "
                + quantity + " exemplaire(s) du livre "
                + book.getTitle()
            );
        } else {
            System.out.println(
                clientName + " ❌ achat refusé (stock insuffisant)"
            );
        }
    }
}
