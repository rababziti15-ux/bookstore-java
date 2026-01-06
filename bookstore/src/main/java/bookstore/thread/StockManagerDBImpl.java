package bookstore.thread;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import bookstore.model.Book;
import config.DBConnection;

public class StockManagerDBImpl implements StockManager {

    @Override
    public boolean buyBook(Book book, int quantity) {

        String selectSql =
            "SELECT stock FROM books WHERE isbn = ? FOR UPDATE";
        String updateSql =
            "UPDATE books SET stock = stock - ? WHERE isbn = ?";

        try (Connection conn = DBConnection.getConnection()) {

            conn.setAutoCommit(false);

            PreparedStatement psSelect =
                conn.prepareStatement(selectSql);
            psSelect.setString(1, book.getIsbn());

            ResultSet rs = psSelect.executeQuery();

            if (!rs.next()) {
                conn.rollback();
                return false;
            }

            int stock = rs.getInt("stock");

            if (stock < quantity) {
                conn.rollback();
                return false;
            }

            PreparedStatement psUpdate =
                conn.prepareStatement(updateSql);
            psUpdate.setInt(1, quantity);
            psUpdate.setString(2, book.getIsbn());
            psUpdate.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
