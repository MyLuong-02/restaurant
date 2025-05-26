package org.restaurant.model;

/**
 * @author Luong Thi Tra My
 * @version 1.0
 *
 */
public class Item {
    private int itemId;
    private String name;
    private double price;

    public Item() {
        this.itemId = 0;
        this.name = "";
        this.price = 0.0;
    }

    public Item(int itemId, String name, double price) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
    }

    public Item(String name, double price) {
        this.name = name;
        this.price = price;
    }


    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

}