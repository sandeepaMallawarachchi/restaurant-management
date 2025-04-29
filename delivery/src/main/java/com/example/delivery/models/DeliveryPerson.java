package com.example.delivery.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("delivery_persons")
public class DeliveryPerson {
    @Id
    private String id;
    private String name;
    private String address;
    private String city;
    private String registeredRestaurantId;
    private boolean available = true;
    private List<String> assignedOrderIds = new ArrayList<>();
    private String deliveryStatus;

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRegisteredRestaurantId() { return registeredRestaurantId; }
    public void setRegisteredRestaurantId(String registeredRestaurantId) { this.registeredRestaurantId = registeredRestaurantId; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public List<String> getAssignedOrderIds() { return assignedOrderIds; }
    public void setAssignedOrderIds(List<String> assignedOrderIds) { this.assignedOrderIds = assignedOrderIds; }

    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }
}
