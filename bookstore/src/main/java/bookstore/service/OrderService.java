package bookstore.service;

import bookstore.model.Order;
import bookstore.model.OrderItem;
import java.util.List;

public interface OrderService {
    Order createOrder(List<OrderItem> items);
    double calculateTotal(Order order);
}
