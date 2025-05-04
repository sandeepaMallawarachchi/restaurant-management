package com.example.delivery.models;

public class RestaurantDTO {
    private String id;
    private String name;
    private String city;
    private boolean available;
    private boolean verifiedByAdmin;

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    //
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public boolean isVerifiedByAdmin() { return verifiedByAdmin; }
    public void setVerifiedByAdmin(boolean verifiedByAdmin) { this.verifiedByAdmin = verifiedByAdmin; }
}
