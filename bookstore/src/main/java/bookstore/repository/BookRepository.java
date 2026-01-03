package bookstore.repository;

import bookstore.model.Book;
import java.util.List;

public interface BookRepository {
    List<Book> findAll();
}
