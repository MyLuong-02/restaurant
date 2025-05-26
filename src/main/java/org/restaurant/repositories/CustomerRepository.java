package org.restaurant.repositories;

import org.restaurant.model.Customer;
import org.restaurant.utils.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    public void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (name, address, phone) VALUES (?, ?, ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.executeUpdate();
            System.out.println("Customer added successfully.");
        } catch (SQLException e) {
            System.err.println("Error adding customer: " + e.getMessage());
        }
    }

    public void updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET name = ?, address = ?, phone = ? WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getAddress());
            stmt.setString(3, customer.getPhoneNumber());
            stmt.setInt(4, customer.getCustomerId());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Customer updated successfully.");
            } else {
                System.out.println("No customer found with ID: " + customer.getCustomerId());
            }
        } catch (SQLException e) {
            System.err.println("Error updating customer: " + e.getMessage());
        }
    }

    public List<Customer> searchCustomer(String keyword, String orderBy, boolean ascending) throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer WHERE name ILIKE ? OR phone ILIKE ? ORDER BY " + orderBy + (ascending ? " ASC" : " DESC");

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Error searching customer: " + e.getMessage());
        }

        return customers;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customer ORDER BY id ASC";
        List<Customer> customers = new ArrayList<>();
        try (Connection con = DbConnection.getConnection();
                PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone")
                ));
            }
        }
        return customers;
    }

    //Get customer by name
    public Customer getCustomerByName(String name) throws SQLException {
        String sql = "SELECT * FROM customer WHERE name = ?";
        Customer customer = null;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting customer by name: " + e.getMessage());
        }

        return customer;
    }

}
