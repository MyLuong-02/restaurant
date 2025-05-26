package org.restaurant.model;

import java.util.Date;
import java.util.List;

public class Order {
    private int id;
    private double totalPrice;
    private Date createdDate;
    private Customer customer;
    private List<Item> items;
    private Deliveryman deliveryman;

    public Order() {
        this.id = 0;
        this.totalPrice = 0.0;
        this.createdDate = new Date();
        this.customer = new Customer();
        this.items = null;
        this.deliveryman = new Deliveryman();
    }

    public Order(int id, double totalPrice, Date createdDate, Customer customer, List<Item> items, Deliveryman deliveryman) {
        this.id = id;
        this.totalPrice = totalPrice;
        this.createdDate = createdDate;
        this.customer = customer;
        this.items = items;
        this.deliveryman = deliveryman;
    }

    public Order(double totalPrice, Date createdDate, Customer customer, List<Item> items, Deliveryman deliveryman) {
        this.totalPrice = totalPrice;
        this.createdDate = createdDate;
        this.customer = customer;
        this.items = items;
        this.deliveryman = deliveryman;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Deliveryman getDeliveryman() {
        return deliveryman;
    }

    public void setDeliveryman(Deliveryman deliveryman) {
        this.deliveryman = deliveryman;
    }
}
