package com.restaurant.restaurant.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "menu_items")
public class MenuItem {
    @Id
    private String id;

    @NotNull(message = "UserId is required")
    private Long userId;

    @NotNull(message = "RestaurantId is required")
    private String restaurantId;

    @Indexed(unique = true)
    @NotBlank(message = "Restaurant name is required")
    private String name;

    @NotNull(message = "Price is required")
    private Double price;

    private String description;

    @NotNull(message = "Category is required")
    private String category;

    private String imageUrl;

    private boolean available = true;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }
}
