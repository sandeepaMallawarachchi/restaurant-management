package com.restaurant.restaurant.services;

import com.restaurant.restaurant.models.MenuItem;
import com.restaurant.restaurant.models.Restaurant;
import com.restaurant.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(String id) {
        return restaurantRepository.findById(id);
    }

    public Restaurant patchRestaurant(String id, Map<String, Object> updates) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> restaurant.setName((String) value);
                case "email" -> restaurant.setEmail((String) value);
                case "password" -> restaurant.setPassword((String) value);
                case "address" -> restaurant.setAddress((String) value);
                case "city" -> restaurant.setCity((String) value);
                case "postal" -> restaurant.setPostal((String) value);
                case "available" -> restaurant.setAvailable((Boolean) value);
                case "verifiedByAdmin" -> restaurant.setVerifiedByAdmin((Boolean) value);
                case "menu" -> restaurant.setMenu((List<MenuItem>) value);
            }
        });

        return restaurantRepository.save(restaurant);
    }

    public void deleteRestaurant(String id) {
        restaurantRepository.deleteById(id);
    }
}