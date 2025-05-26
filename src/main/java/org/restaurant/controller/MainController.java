package org.restaurant.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;

import java.io.IOException;

public class MainController {

    @FXML
    private Tab customerTab;

    @FXML
    private Tab itemTab;

    @FXML
    private Tab deliverymenTab;

    @FXML
    private Tab ordersTab;

    @FXML
    public void initialize() {
        try {
            // Load CustomerView.fxml and set it as the content of customerTab
            Parent customerContent = FXMLLoader.load(getClass().getResource("/org/restaurant/CustomerView.fxml"));
            customerTab.setContent(customerContent);

            // Load ItemView.fxml and set it as the content of itemTab
            Parent itemContent = FXMLLoader.load(getClass().getResource("/org/restaurant/ItemView.fxml"));
            itemTab.setContent(itemContent);

            //Load DeliverymanView.fxml
            Parent deliverymanContent = FXMLLoader.load(getClass().getResource("/org/restaurant/DeliverymanView.fxml"));
            deliverymenTab.setContent(deliverymanContent);

            //Load OrderView.fxml
            Parent orderContent = FXMLLoader.load(getClass().getResource("/org/restaurant/OrderView.fxml"));
            ordersTab.setContent(orderContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
