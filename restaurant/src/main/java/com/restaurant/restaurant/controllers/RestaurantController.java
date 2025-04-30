package com.restaurant.restaurant.controllers;

import com.restaurant.restaurant.models.Restaurant;
import com.restaurant.restaurant.services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @PostMapping("/add")
    public ResponseEntity<?> addRestaurant(@RequestBody Restaurant restaurant) {
        try {
            Restaurant saved = restaurantService.addRestaurant(restaurant);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String id) {
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(id);
        return restaurant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Restaurant>> getRestaurantByUserId(@PathVariable Long userId) {
        List<Restaurant> items = restaurantService.findByUserId(userId);
        if (items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(items);
    }

    @PreAuthorize("@restaurantSecurity.isOwnerOrAdmin(authentication, #id)")
    @PatchMapping("/{id}")
    public ResponseEntity<Restaurant> patchRestaurant(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        Restaurant updatedRestaurant = restaurantService.patchRestaurant(id, updates);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @PreAuthorize("@restaurantSecurity.isOwnerOrAdmin(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRestaurant(@PathVariable String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.ok("Restaurant deleted successfully");
    }
}
