package org.restaurant.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.restaurant.model.*;
import org.restaurant.repositories.*;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.*;

public class OrderController {

    @FXML
    private ComboBox<String> cmbItem;
    @FXML
    private ComboBox<String> cmbCustomer;
    @FXML
    private ComboBox<String> cmbDeliveryman;
    @FXML
    private TextField totalPrice;
    @FXML
    private DatePicker createdDate;
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
    private TableView<Order> itemsTable;
    @FXML
    private TableColumn<Order, Integer> cId;
    @FXML
    private TableColumn<Order, String> cItem;
    @FXML
    private TableColumn<Order, Double> cTotalPrice;
    @FXML
    private TableColumn<Order, String> cCustomer;
    @FXML
    private TableColumn<Order, String> cDeliveryman;
    @FXML
    private TableColumn<Order, Date> cCreatedDate;

    private final OrderRepository orderRepository = new OrderRepository();
    private final ItemRepository itemRepository = new ItemRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final DeliverymanRepository deliverymanRepository = new DeliverymanRepository();
    private final ObservableList<Order> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupFilterComboBox();
        loadComboBoxData();
        setupItemSelectionListener();
        refreshOrderList();
    }

    private void setupTableColumns() {
        cId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getId()).asObject());

        cItem.setCellValueFactory(cell -> {
            List<Item> items = cell.getValue().getItems();
            String name = (items != null && !items.isEmpty() && items.get(0) != null)
                    ? items.get(0).getName()
                    : "N/A";
            return new SimpleStringProperty(name);
        });

        cTotalPrice.setCellValueFactory(cell -> new SimpleDoubleProperty(cell.getValue().getTotalPrice()).asObject());

        cCustomer.setCellValueFactory(cell -> {
            Customer customer = cell.getValue().getCustomer();
            return new SimpleStringProperty(customer != null ? customer.getName() : "N/A");
        });

        cDeliveryman.setCellValueFactory(cell -> {
            Deliveryman dm = cell.getValue().getDeliveryman();
            return new SimpleStringProperty(dm != null ? dm.getName() : "N/A");
        });

        cCreatedDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCreatedDate()));

        itemsTable.setItems(data);
    }

    private void setupFilterComboBox() {
        cmbFilterBy.setItems(FXCollections.observableArrayList("id", "createdDate"));
        cmbFilterBy.getSelectionModel().selectFirst();
    }

    private void loadComboBoxData() {
        try {
            // Load and set item names
            List<String> itemNames = itemRepository.getAllItems()
                    .stream().map(Item::getName).collect(Collectors.toList());
            cmbItem.setItems(FXCollections.observableArrayList(itemNames));
            cmbItem.getSelectionModel().clearSelection(); // prevent auto-select

            // Load and set customer names
            List<String> customerNames = customerRepository.getAllCustomers()
                    .stream().map(Customer::getName).collect(Collectors.toList());
            cmbCustomer.setItems(FXCollections.observableArrayList(customerNames));

            // Load and set deliveryman names
            List<String> deliverymanNames = deliverymanRepository.getAllDeliverymen()
                    .stream().map(Deliveryman::getName).collect(Collectors.toList());
            cmbDeliveryman.setItems(FXCollections.observableArrayList(deliverymanNames));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Data", e.getMessage());
        }
    }

    private void setupItemSelectionListener() {
        cmbItem.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if (newItem != null) {
                try {
                    Item selectedItem = itemRepository.getItemByName(newItem);
                    if (selectedItem != null) {
                        totalPrice.setText(String.format("%.2f", selectedItem.getPrice()));
                    } else {
                        totalPrice.clear();
                    }
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
                }
            } else {
                totalPrice.clear();
            }
        });
    }


    @FXML
    public void addOrder() {
        try {
            Order order = buildOrderFromForm();
            if (order == null) return;

            orderRepository.addOrder(order);
            refreshOrderList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order added successfully.");
            hideOrderForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void updateOrder() {
        Order selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to update.");
            return;
        }

        try {
            Order order = buildOrderFromForm();
            if (order == null) return;

            order.setId(selected.getId());
            orderRepository.updateOrder(order);
            refreshOrderList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order updated successfully.");
            hideOrderForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void deleteOrder() {
        Order selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to delete.");
            return;
        }

        try {
            orderRepository.deleteOrder(selected.getId());
            refreshOrderList();
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Order deleted successfully.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
        }
    }

    @FXML
    public void searchOrders() {
        try {
            String keyword = search.getText();
            String sortBy = cmbFilterBy.getValue();
            boolean asc = ascending.isSelected();
            List<Order> orders = orderRepository.searchOrder(keyword, sortBy, asc);
            data.setAll(orders);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Searching Orders", e.getMessage());
        }
    }

    private void refreshOrderList() {
        try {
            List<Order> orders = orderRepository.getAllOrders();
            data.setAll(orders);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error Loading Orders", e.getMessage());
        }
    }

    private void clearFields() {
        cmbItem.getSelectionModel().clearSelection();
        cmbCustomer.getSelectionModel().clearSelection();
        cmbDeliveryman.getSelectionModel().clearSelection();
        totalPrice.clear();
        createdDate.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void showAddOrderForm() {
        clearFields();
        formBox.setVisible(true);
        formBox.setManaged(true);
        btnAdd.setVisible(true);
        btnAdd.setManaged(true);
        btnUpdate.setVisible(false);
        btnUpdate.setManaged(false);
    }

    private Order buildOrderFromForm() {
        String selectedItemName = cmbItem.getSelectionModel().getSelectedItem();
        String selectedCustomerName = cmbCustomer.getSelectionModel().getSelectedItem();
        String selectedDeliverymanName = cmbDeliveryman.getSelectionModel().getSelectedItem();

        if (selectedItemName == null || selectedCustomerName == null || selectedDeliverymanName == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select an item, customer, and deliveryman.");
            return null;
        }

        if (createdDate.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please select a valid date.");
            return null;
        }

        try {
            // Lookup objects by name
            Item selectedItem = itemRepository.getItemByName(selectedItemName);
            Customer selectedCustomer = customerRepository.getCustomerByName(selectedCustomerName);
            Deliveryman selectedDeliveryman = deliverymanRepository.getDeliverymanByName(selectedDeliverymanName);

            if (selectedItem == null || selectedCustomer == null || selectedDeliveryman == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Selected item, customer, or deliveryman not found.");
                return null;
            }

double price = selectedItem.getPrice();
            Date date = java.sql.Date.valueOf(createdDate.getValue());

            // Return the constructed Order object
            return new Order(price, date, selectedCustomer, List.of(selectedItem), selectedDeliveryman);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            return null;
        }
    }

    @FXML
    public void showUpdateOrderForm() {
        Order selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to update.");
            return;
        }

        // Select first item name in ComboBox
        if (selected.getItems() != null && !selected.getItems().isEmpty()) {
            cmbItem.getSelectionModel().select(selected.getItems().get(0).getName());
        } else {
            cmbItem.getSelectionModel().clearSelection();
        }

        cmbCustomer.getSelectionModel().select(selected.getCustomer() != null ? selected.getCustomer().getName() : null);
        cmbDeliveryman.getSelectionModel().select(selected.getDeliveryman() != null ? selected.getDeliveryman().getName() : null);

        totalPrice.setText(String.valueOf(selected.getTotalPrice()));

        if (selected.getCreatedDate() != null) {
            createdDate.setValue(new java.sql.Date(selected.getCreatedDate().getTime()).toLocalDate());
        } else {
            createdDate.setValue(null);
        }

        formBox.setVisible(true);
        formBox.setManaged(true);
        btnAdd.setVisible(false);
        btnAdd.setManaged(false);
        btnUpdate.setVisible(true);
        btnUpdate.setManaged(true);
    }

    @FXML
    public void hideOrderForm() {
        formBox.setVisible(false);
        formBox.setManaged(false);
        clearFields();
    }
}
