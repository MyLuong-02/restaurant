package org.restaurant.model;
import java.util.List;

public class Deliveryman {
    private int id;
    private String name;
    private String phoneNumber;
    private List<Order> orders;


    public Deliveryman() {
        this.id = 0;
        this.name = "";
        this.phoneNumber = "";
        this.orders = null;
    }

    public Deliveryman(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Deliveryman(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
