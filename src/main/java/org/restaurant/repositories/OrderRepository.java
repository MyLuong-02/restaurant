package org.restaurant.repositories;

import org.restaurant.model.Customer;
import org.restaurant.model.Item;
import org.restaurant.model.Order;
import org.restaurant.model.Deliveryman;
import org.restaurant.utils.DbConnection;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class OrderRepository {
    //Add a new order
    public void addOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (total_price, creation_date, item_id, customer_id, deliveryman_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, order.getTotalPrice());
            stmt.setDate(2, new java.sql.Date(order.getCreatedDate().getTime()));
            stmt.setInt(3, order.getItems().get(0).getItemId()); // Assuming the first item in the list
            stmt.setInt(4, order.getCustomer().getCustomerId());
            stmt.setInt(5, order.getDeliveryman().getId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Order added successfully.");
            } else {
                System.out.println("Failed to add order.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding order: " + e.getMessage());
        }
    }

    //Update order
    public void updateOrder(Order order) throws SQLException {
        String sql = "UPDATE orders SET total_price = ?, creation_date = ?, item_id = ?, customer_id = ?, deliveryman_id = ? WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, order.getTotalPrice());
            stmt.setDate(2, new java.sql.Date(order.getCreatedDate().getTime()));
            stmt.setInt(3, order.getItems().get(0).getItemId()); // Assuming the first item in the list
            stmt.setInt(4, order.getCustomer().getCustomerId());
            stmt.setInt(5, order.getDeliveryman().getId());
            stmt.setInt(6, order.getId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Order updated successfully.");
            } else {
                System.out.println("No order found with ID: " + order.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
        }
    }

    //Delete order
    public void deleteOrder(int id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Order deleted successfully.");
            } else {
                System.out.println("No order found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
        }
    }

    //Search order by id or creation_date.
    public List<Order> searchOrder(String keyword, String orderBy, boolean ascending) throws SQLException {
        List<Order> orders = new ArrayList<>();

        // Define allowed columns and map to fully qualified names
        Map<String, String> allowedOrderBy = Map.of(
                "id", "o.id",
                "creation_date", "o.creation_date",
                "total_price", "o.total_price",
                "item_name", "i.name",
                "customer_name", "c.name",
                "deliveryman_name", "d.name"
        );

        // Validate orderBy parameter and default to o.id if invalid
        String orderByQualified = allowedOrderBy.getOrDefault(orderBy, "o.id");

        String sql = "SELECT o.id AS order_id, o.creation_date, o.total_price, " +
                "i.name AS item_name, c.name AS customer_name, d.name AS deliveryman_name " +
                "FROM orders o " +
                "JOIN item i ON o.item_id = i.id " +
                "LEFT JOIN customer c ON o.customer_id = c.id " +
                "LEFT JOIN deliveryman d ON o.deliveryman_id = d.id " +
                "WHERE CAST(o.id AS TEXT) ILIKE ? OR CAST(o.creation_date AS TEXT) ILIKE ? " +
                "ORDER BY " + orderByQualified + (ascending ? " ASC" : " DESC");

        try (Connection con = DbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("order_id"));
                order.setCreatedDate(rs.getDate("creation_date"));
                order.setTotalPrice(rs.getDouble("total_price"));

                Customer customer = new Customer();
                customer.setName(rs.getString("customer_name"));
                order.setCustomer(customer);

                List<Item> items = new ArrayList<>();
                Item item = new Item();
                item.setName(rs.getString("item_name"));
                items.add(item);
                order.setItems(items);

                Deliveryman deliveryman = new Deliveryman();
                deliveryman.setName(rs.getString("deliveryman_name"));
                order.setDeliveryman(deliveryman);

                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error searching order: " + e.getMessage());
        }
        return orders;
    }

    //Get all orders
    public List<Order> getAllOrders() throws SQLException {
        String sql = "SELECT o.id AS order_id, o.creation_date, o.total_price, i.name AS item_name, c.name AS customer_name, d.name AS deliveryman_name FROM orders o JOIN item i ON o.item_id = i.id LEFT JOIN customer c ON o.customer_id = c.id LEFT JOIN deliveryman d ON o.deliveryman_id = d.id ";

        List<Order> orders = new ArrayList<>();
        try (Connection con = DbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("order_id"));
                order.setCreatedDate(rs.getDate("creation_date"));
                order.setTotalPrice(rs.getDouble("total_price"));
                Customer customer = new Customer();
                customer.setName(rs.getString("customer_name"));
                order.setCustomer(customer);
                List<Item> items = new ArrayList<>();
                Item item = new Item();
                item.setName(rs.getString("item_name"));
                items.add(item);
                order.setItems(items);
                Deliveryman deliveryman = new Deliveryman();
                deliveryman.setName(rs.getString("deliveryman_name"));
                order.setDeliveryman(deliveryman);
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        return orders;
    }
}