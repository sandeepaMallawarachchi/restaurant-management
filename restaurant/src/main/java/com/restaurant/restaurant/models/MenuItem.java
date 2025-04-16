package com.restaurant.restaurant.models;

public class MenuItem {
    private String name;
    private double price;
    private String description;
    private boolean isAvailable;

    public MenuItem() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
}
