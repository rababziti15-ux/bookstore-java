package bookstore.repository;

import bookstore.model.Book;
import java.util.List;
import java.util.Map;

public interface BookRepository {

    // ================= ORIGINAL METHODS =================
    List<Book> findAll();

    Book findByIsbn(String isbn);

    void save(Book book);

    boolean deleteByIsbn(String isbn);

    // ================= NEW METHODS =================
    
    // Batch operations
    void saveAll(List<Book> books);
    
    boolean updateBook(String isbn, Book updatedBook);

    // Availability queries
    List<Book> findAvailableBooks();
    
    List<Book> findOutOfStockBooks();
    
    List<Book> findLowStockBooks(int threshold);

    // Price queries
    Book findMostExpensiveBook();
    
    Book findCheapestBook();
    
    List<Book> findBooksByPriceRange(double min, double max);
    
    List<Book> findTopExpensiveBooks(int n);
    
    double averageBookPrice();

    // Author queries
    List<Book> findBooksByAuthor(String author);
    
    Map<String, List<Book>> groupBooksByAuthor();
    
    List<String> getAllAuthors();

    // Search and categorization
    List<Book> searchByTitle(String keyword);
    
    Map<String, Long> countByPriceCategory();

    // Stock analytics
    double totalStockValue();
    
    long getTotalBooksCount();
}
