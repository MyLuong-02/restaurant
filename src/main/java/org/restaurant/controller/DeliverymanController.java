package org.restaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.restaurant.repositories.DeliverymanRepository;
import org.restaurant.model.Deliveryman;

import java.sql.SQLException;
import java.util.List;

public class DeliverymanController {

    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;

    @FXML
    private HBox formContainer;
    @FXML
    private Button btnSubmitAdd;
    @FXML
    private Button btnSubmitUpdate;
    @FXML
    private Button btnDelete;
        @FXML
    private TextField txtSearch;
    @FXML
    private ComboBox<String> cmbSortBy;
    @FXML
    private CheckBox chkAscending;
    @FXML
    private TableView<Deliveryman> table;
    @FXML
    private TableColumn<Deliveryman, Integer> colId;
    @FXML
    private TableColumn<Deliveryman, String> colName;
    @FXML
    private TableColumn<Deliveryman, String> colPhone;

    private final DeliverymanRepository deliverymanRepository = new DeliverymanRepository();
    private final ObservableList<Deliveryman> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        colName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        colPhone.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getPhoneNumber()));

        table.setItems(data);

        cmbSortBy.getItems().addAll("name", "phone");
        cmbSortBy.getSelectionModel().selectFirst();

        // 👇 Add listener to populate fields on row selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtName.setText(newSelection.getName());
                txtPhone.setText(newSelection.getPhoneNumber());
            }
        });

        refreshDeliverymanList();
    }

    @FXML
    public void addDeliveryman() {
        try {
            String name = txtName.getText().trim();
            String phone = txtPhone.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Name and phone cannot be empty.");
                return;
            }

deliverymanRepository.addDeliveryman(new Deliveryman(name, phone));
            refreshDeliverymanList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Deliveryman added successfully.");
            hideForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void updateDeliveryman() {
        Deliveryman selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a deliveryman to update.");
            return;
        }

        try {
            // Only update fields if the user entered something; otherwise keep old values
            String newName = txtName.getText().trim();
            String newPhone = txtPhone.getText().trim();

            if (!newName.isEmpty()) {
                selected.setName(newName);
            }
            if (!newPhone.isEmpty()) {
                selected.setPhoneNumber(newPhone);
            }

            // Validate required fields after update
            if (selected.getName().isEmpty() || selected.getPhoneNumber().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Name and phone cannot be empty.");
                return;
            }

            deliverymanRepository.updateDeliveryman(selected);
            refreshDeliverymanList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Deliveryman updated successfully.");
            hideForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void deleteDeliveryman(){
        Deliveryman selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a deliveryman to delete.");
            return;
        }

        try {
            deliverymanRepository.deleteDeliveryman(selected.getId());
            refreshDeliverymanList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Deliveryman deleted successfully.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void searchDeliveryman() {
        try {
            String keyword = txtSearch.getText();
            String sortBy = cmbSortBy.getValue();
            boolean ascending = chkAscending.isSelected();

            if (keyword.isEmpty()) {
                // If search field is empty, show all customers
                refreshDeliverymanList();
            } else {
                // Perform filtered search
                List<Deliveryman> deliverymans = deliverymanRepository.searchDeliveryman(keyword, sortBy, ascending);
                data.setAll(deliverymans);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", e.getMessage());
        }
    }

    private void refreshDeliverymanList() {
        try {
            String keyword = txtSearch.getText();
            String sortBy = cmbSortBy.getValue();
            boolean ascending = chkAscending.isSelected();
            List<Deliveryman> deliverymans = deliverymanRepository.getAllDeliverymen();
            data.setAll(deliverymans);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", e.getMessage());
        }
    }

    private void clearFields() {
        txtName.clear();
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
        Deliveryman selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a customer to update.");
            return;
        }
        formContainer.setVisible(true);
        formContainer.setManaged(true);
        txtName.setText(selected.getName());
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