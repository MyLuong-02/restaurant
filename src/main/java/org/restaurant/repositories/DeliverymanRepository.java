package org.restaurant.repositories;

import org.restaurant.model.Deliveryman;
import org.restaurant.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeliverymanRepository {
    public void addDeliveryman(Deliveryman deliveryman) throws SQLException {
        String sql = "INSERT INTO deliveryman (name, phone) VALUES (?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deliveryman.getName());
            stmt.setString(2, deliveryman.getPhoneNumber());
            stmt.executeUpdate();
            System.out.println("Deliveryman added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding deliveryman: " + e.getMessage());
        }
    }

    public void updateDeliveryman(Deliveryman deliveryman) throws SQLException {
        String sql = "UPDATE deliveryman SET name = ?, phone = ? WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deliveryman.getName());
            stmt.setString(2, deliveryman.getPhoneNumber());
            stmt.setInt(3, deliveryman.getId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Deliveryman updated successfully.");
            } else {
                System.out.println("No deliveryman found with ID: " + deliveryman.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating deliveryman" + e.getMessage());
        }
    }

    //Delete deliveryman
    public void deleteDeliveryman(int id) throws SQLException {
        String sql = "DELETE FROM deliveryman WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Deliveryman deleted successfully.");
            } else {
                System.out.println("No deliveryman found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Error deleting deliveryman: " + e.getMessage());
        }
    }

    //Search deliveryman
    public List<Deliveryman> searchDeliveryman(String keyword, String orderBy, boolean ascending) throws SQLException {
        List<Deliveryman> deliverymen = new ArrayList<>();
        String sql = "SELECT * FROM deliveryman WHERE name ILIKE ? OR phone ILIKE ? ORDER BY " + orderBy + (ascending ? " ASC" : " DESC");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Deliveryman deliveryman = new Deliveryman(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone")
                );
                deliverymen.add(deliveryman);
            }
        }
        return deliverymen;
    }


    //Get all deliverymen
    public List<Deliveryman> getAllDeliverymen() throws SQLException {
        String sql = "SELECT * FROM deliveryman ORDER BY id ASC";
        List<Deliveryman> deliverymen = new ArrayList<>();
        try (Connection con = DbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                deliverymen.add(new Deliveryman(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone")
                ));
            }
        }
        return deliverymen;
    }


    //Get deliveryman by name
    public Deliveryman getDeliverymanByName(String name) throws SQLException {
        String sql = "SELECT * FROM deliveryman WHERE name = ?";
        Deliveryman deliveryman = null;
        try (Connection con = DbConnection.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                deliveryman = new Deliveryman(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone")
                );
            }
        }
        return deliveryman;
    }

}