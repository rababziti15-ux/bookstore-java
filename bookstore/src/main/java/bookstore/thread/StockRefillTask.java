package bookstore.thread;

import bookstore.model.Book;
import config.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StockRefillTask implements Runnable {

    private final Book book;
    private final int threshold;
    private final int refillAmount;

    public StockRefillTask(Book book, int threshold, int refillAmount) {
        this.book = book;
        this.threshold = threshold;
        this.refillAmount = refillAmount;
    }

    @Override
    public void run() {

        synchronized (book) {
            try (Connection cn = DBConnection.getConnection()) {

                // 1️⃣ Read real stock from DB
                PreparedStatement psCheck =
                        cn.prepareStatement(
                                "SELECT stock FROM books WHERE isbn = ?"
                        );
                psCheck.setString(1, book.getIsbn());
                ResultSet rs = psCheck.executeQuery();

                if (!rs.next()) return;

                int stock = rs.getInt("stock");

                if (stock <= threshold) {

                    System.out.println(
                            Thread.currentThread().getName()
                            + " → Stock low for book: "
                            + book.getTitle()
                    );

                    // 2️⃣ Update stock in DB
                    PreparedStatement psUpdate =
                            cn.prepareStatement(
                                    "UPDATE books SET stock = stock + ? WHERE isbn = ?"
                            );
                    psUpdate.setInt(1, refillAmount);
                    psUpdate.setString(2, book.getIsbn());
                    psUpdate.executeUpdate();

                    System.out.println(
                            "Stock refilled. New stock = "
                            + (stock + refillAmount)
                    );
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
