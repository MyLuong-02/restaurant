package org.restaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.restaurant.repositories.CustomerRepository;
import org.restaurant.model.Customer;

import java.sql.SQLException;
import java.util.List;

public class CustomerController {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtPhone;

    @FXML
    private HBox formContainer;
    @FXML
    private Button btnSubmitAdd;
    @FXML
    private Button btnSubmitUpdate;
    @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbSortBy;
    @FXML
    private CheckBox chkAscending;
    @FXML
    private TableView<Customer> table;
    @FXML
    private TableColumn<Customer, Integer> colId;
    @FXML
    private TableColumn<Customer, String> colName;
    @FXML
    private TableColumn<Customer, String> colAddress;
    @FXML
    private TableColumn<Customer, String> colPhone;

    private final CustomerRepository customerRepository = new CustomerRepository();
    private final ObservableList<Customer> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getCustomerId()).asObject());
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        colAddress.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getAddress()));
        colPhone.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPhoneNumber()));

        table.setItems(data);

        cmbSortBy.getItems().addAll("name", "phone");
        cmbSortBy.getSelectionModel().selectFirst();

        // 👇 Add listener to populate fields on row selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getName());
                txtAddress.setText(newSelection.getAddress());
                txtPhone.setText(newSelection.getPhoneNumber());
            }
        });

        refreshCustomerList();
    }

    @FXML
    public void addCustomer() {
        try {
            String name = txtName.getText().trim();
            String address = txtAddress.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Name and phone cannot be empty.");
                return;
            }

            customerRepository.addCustomer(new Customer(name, address, phone));
            refreshCustomerList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer added successfully.");
            hideForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void updateCustomer() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to update.");
            return;
        }

        try {
            // Only update fields if the user entered something; otherwise keep old values
            String newName = txtName.getText().trim();
            String newAddress = txtAddress.getText().trim();
            String newPhone = txtPhone.getText().trim();

            if (!newName.isEmpty()) {
                selected.setName(newName);
            }
            if (!newAddress.isEmpty()) {
                selected.setAddress(newAddress);
            }
            if (!newPhone.isEmpty()) {
                selected.setPhoneNumber(newPhone);
            }

            // Validate required fields after update
            if (selected.getName().isEmpty() || selected.getPhoneNumber().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Name and phone cannot be empty.");
                return;
            }

            customerRepository.updateCustomer(selected);
            refreshCustomerList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Customer updated successfully.");
            hideForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void searchCustomer() {
        try {
            String keyword = txtSearch.getText();
            String sortBy = cmbSortBy.getValue();
            boolean ascending = chkAscending.isSelected();

            if (keyword.isEmpty()) {
                // If search field is empty, show all customers
                refreshCustomerList();
            } else {
                // Perform filtered search
                List<Customer> customers = customerRepository.searchCustomer(keyword, sortBy, ascending);
                data.setAll(customers);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", e.getMessage());
        }
    }

    private void refreshCustomerList() {
        try {
            String keyword = txtSearch.getText();
            String sortBy = cmbSortBy.getValue();
            boolean ascending = chkAscending.isSelected();
            List<Customer> customers = customerRepository.getAllCustomers();
            data.setAll(customers);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", e.getMessage());
        }
    }

    private void clearFields() {
        txtName.clear();
        txtAddress.clear();
        txtPhone.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void showAddForm() {
        clearFields();
        formContainer.setVisible(true);
        formContainer.setManaged(true);
        btnSubmitAdd.setVisible(true);
        btnSubmitAdd.setManaged(true);

        btnSubmitUpdate.setVisible(false);
        btnSubmitUpdate.setManaged(false);
    }

    @FXML
    public void showUpdateForm() {
        Customer selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to update.");
            return;
        }
        formContainer.setVisible(true);
        formContainer.setManaged(true);
        txtName.setText(selected.getName());
        txtAddress.setText(selected.getAddress());
        txtPhone.setText(selected.getPhoneNumber());

        btnSubmitAdd.setVisible(false);
        btnSubmitAdd.setManaged(false);

        btnSubmitUpdate.setVisible(true);
        btnSubmitUpdate.setManaged(true);
    }

    @FXML
    public void hideForm() {
        formContainer.setVisible(false);
        formContainer.setManaged(false);
        clearFields();
    }

}