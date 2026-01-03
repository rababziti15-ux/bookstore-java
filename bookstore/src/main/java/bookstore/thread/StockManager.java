package bookstore.thread;

import bookstore.model.Book;

public interface StockManager {
    boolean buyBook(Book book, int quantity);
}
