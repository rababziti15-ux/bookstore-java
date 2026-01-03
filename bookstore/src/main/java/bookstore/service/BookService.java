package bookstore.service;

import bookstore.model.Book;
import java.util.List;

public interface BookService {
    List<Book> getAllBooks();
    List<Book> searchByTitle(String keyword);
    List<Book> filterByPrice(double maxPrice);
}
