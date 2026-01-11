package bookstore.service;

import bookstore.model.Book;
import bookstore.model.Order;
import bookstore.model.OrderItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

public class StatisticsService {

    /* ================= BOOK STATISTICS ================= */

    public long countBooks(List<Book> books) {
        return books.size();
    }

    public OptionalDouble averageBookPrice(List<Book> books) {
        return books.stream()
                .mapToDouble(Book::getPrice)
                .average();
    }

    public Book mostExpensiveBook(List<Book> books) {
        return books.stream()
                .max(Comparator.comparingDouble(Book::getPrice))
                .orElse(null);
    }

    public Book cheapestBook(List<Book> books) {
        return books.stream()
                .min(Comparator.comparingDouble(Book::getPrice))
                .orElse(null);
    }

    public int totalStock(List<Book> books) {
        return books.stream()
                .mapToInt(Book::getStock)
                .sum();
    }

  /* ================= DB ORDER / SALES STATISTICS ================= */

public long countOrdersFromDB(Connection cn) throws Exception {
    ResultSet rs = cn.createStatement()
            .executeQuery("SELECT COUNT(*) FROM orders");
    rs.next();
    return rs.getLong(1);
}

public long countOrderItemsFromDB(Connection cn) throws Exception {
    ResultSet rs = cn.createStatement()
            .executeQuery("SELECT COUNT(*) FROM order_items");
    rs.next();
    return rs.getLong(1);
}

public int totalBooksSoldFromDB(Connection cn) throws Exception {
    ResultSet rs = cn.createStatement()
            .executeQuery("SELECT COALESCE(SUM(quantity), 0) FROM order_items");
    rs.next();
    return rs.getInt(1);
}

public double totalSalesFromDB(Connection cn) throws Exception {
    ResultSet rs = cn.createStatement()
            .executeQuery(
                "SELECT COALESCE(SUM(quantity * price), 0) FROM order_items"
            );
    rs.next();
    return rs.getDouble(1);
}

}
