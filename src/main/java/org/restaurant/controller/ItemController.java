package org.restaurant.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.restaurant.repositories.ItemRepository;
import org.restaurant.model.Item;

import java.sql.SQLException;
import java.util.List;

public class ItemController {

    @FXML
    private TextField name;
    @FXML
    private TextField price;

    @FXML
    private HBox formBox;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnUpdate;
        @FXML
    private Button btnDelete;
    @FXML
    private TextField search;
    @FXML
    private ComboBox<String> cmbFilterBy;
    @FXML
    private CheckBox ascending;
    @FXML
    private TableView<Item> itemsTable;
    @FXML
    private TableColumn<Item, Integer> cId;
    @FXML
    private TableColumn<Item, String> cName;
    @FXML
    private TableColumn<Item, Double> cPrice;

    private final ItemRepository itemRepository = new ItemRepository();
    private final ObservableList<Item> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
cId.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getItemId()).asObject());
        cName.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        cPrice.setCellValueFactory(cell -> new javafx.beans.property.SimpleDoubleProperty(cell.getValue().getPrice()).asObject());

itemsTable.setItems(data);

        cmbFilterBy.getItems().addAll("name", "price");
        cmbFilterBy.getSelectionModel().selectFirst();

        // 👇 Add listener to populate fields on row selection
        itemsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                name.setText(newSelection.getName());
                price.setText(String.valueOf(newSelection.getPrice()));

            }
                    });

        refreshItemList();
    }

    @FXML
    public void addItem() {
        try {
            String itemName = name.getText().trim();
            String priceText = price.getText().trim().replace(",", "");  // Remove commas

            if (itemName.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Name cannot be empty.");
                return;
            }

            double itemPrice;
            try {
                itemPrice = Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid price format. Please enter a valid number.");
                return;
            }

            if (itemPrice <= 0) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Price must be greater than zero.");
                return;
            }

            itemRepository.addItem(new Item(itemName, itemPrice));
            refreshItemList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Item added successfully.");
hideItemForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void updateItem() {
        Item selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to update.");
            return;
        }

        try {
            // Only update fields if the user entered something; otherwise keep old values
            String newName = name.getText().trim();
            String priceText = price.getText().trim().replace(",", "");  // Remove commas

            if (!newName.isEmpty()) {
                selected.setName(newName);
            }

            if (!priceText.isEmpty()) {
                double newPrice;
                try {
                    newPrice = Double.parseDouble(priceText);
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Invalid price format. Please enter a valid number.");
                    return;
                }

                if (newPrice <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Validation Error", "Price must be greater than zero.");
                    return;
                }

                selected.setPrice(newPrice);
            }

            // Final validation to ensure required fields are not empty
            if (selected.getName().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Name cannot be empty.");
                return;
            }

            itemRepository.updateItem(selected);
            refreshItemList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Item updated successfully.");
            hideItemForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    //Delete item
        @FXML
        public void deleteItem() {
            Item selected = itemsTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item to delete.");
                return;
            }

            try {
                itemRepository.deleteItem(selected.getItemId());
                refreshItemList();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item deleted successfully.");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            }
        }

    @FXML
    public void searchItems() {
        try {
            String keyword = search.getText();
            String sortBy = cmbFilterBy.getValue();
            boolean asc = ascending.isSelected();

            if (keyword.isEmpty()) {
                // If search field is empty, show all customers
                refreshItemList();
            } else {
                // Perform filtered search
                List<Item> items = itemRepository.searchItems(keyword, sortBy, asc);
                data.setAll(items);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", e.getMessage());
        }
    }

    private void refreshItemList() {
        try {
            String keyword = search.getText();
            String sortBy =cmbFilterBy.getValue();
            boolean asc = ascending.isSelected();
            List<Item> items = itemRepository.getAllItems();
            data.setAll(items);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", e.getMessage());
        }
    }

    private void clearFields() {
        name.clear();
        price.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void showAddItemForm() {
        clearFields();
        formBox.setVisible(true);
        formBox.setManaged(true);
        btnAdd.setVisible(true);
        btnAdd.setManaged(true);

        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
    }

    @FXML
    public void showUpdateItemForm() {
Item selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an item update.");
            return;
        }
formBox.setVisible(true);
formBox.setManaged(true);
name.setText(selected.getName());
price.setText(String.valueOf(selected.getPrice()));

        btnAdd.setVisible(false);
        btnAdd.setManaged(false);

        btnUpdate.setVisible(true);
        btnUpdate.setManaged(true);
    }

    @FXML
    public void hideItemForm() {
        formBox.setVisible(false);
        formBox.setManaged(false);
        clearFields();
    }

}