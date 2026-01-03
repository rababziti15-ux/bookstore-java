package bookstore.thread;

import bookstore.model.Book;

public class PurchaseTask implements Runnable {

    private Book book;
    private int quantity;
    private StockManager stockManager;

    public PurchaseTask(Book book, int quantity, StockManager stockManager) {
        this.book = book;
        this.quantity = quantity;
        this.stockManager = stockManager;
    }

    @Override
    public void run() {
        stockManager.buyBook(book, quantity);
    }
}
