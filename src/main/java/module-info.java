module org.restaurant {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.restaurant to javafx.fxml;
    exports org.restaurant;

    opens org.restaurant.model to javafx.fxml;
    exports org.restaurant.model;

    opens org.restaurant.utils to javafx.fxml;
    exports org.restaurant.utils;

    opens org.restaurant.controller to javafx.fxml;
    exports org.restaurant.controller;

    opens org.restaurant.repositories to javafx.fxml;
    exports org.restaurant.repositories;

}