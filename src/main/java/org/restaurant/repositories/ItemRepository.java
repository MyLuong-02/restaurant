package org.restaurant.repositories;

import org.restaurant.model.Item;
import org.restaurant.utils.DbConnection;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class ItemRepository {
    public void addItem(Item item) throws SQLException {
        String sql = "INSERT INTO item (name, price) VALUES (?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.executeUpdate();
            System.out.println("Item added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding item: " + e.getMessage());
        }
    }

    public void updateItem(Item item) throws SQLException {
        String sql = "UPDATE item SET name = ?, price = ? WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setInt(3, item.getItemId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Item updated successfully.");
            } else {
                System.out.println("Noitem found with ID: " + item.getItemId());
            }
        } catch (SQLException e) {
            System.err.println("Error updatingitem: " + e.getMessage());
        }
    }

    public void deleteItem(int id) throws SQLException {
        String sql = "DELETE FROM item WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Item deleted successfully.");
            } else {
                System.out.println("No item found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting item: " + e.getMessage());
        }
    }

    public List<Item> searchItems(String keyword, String orderBy, boolean ascending) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM item WHERE name ILIKE ? OR CAST(price AS TEXT) ILIKE ? ORDER BY "
                + orderBy + (ascending ? " ASC" : " DESC");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );

                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error searching items: " + e.getMessage());
            throw e;
        }

        return items;
    }

    public List<Item> getAllItems() throws SQLException {
        String sql = "SELECT * FROM item ORDER BY id ASC";
        List<Item> items = new ArrayList<>();
        try (Connection con = DbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );

                items.add(item);

            }
        }
        return items;
    }


    //Get item by name
    public Item getItemByName(String name) throws SQLException {
        String sql = "SELECT * FROM item WHERE name = ?";
        Item item = null;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                );
            }
        }
        return item;
    }

}
