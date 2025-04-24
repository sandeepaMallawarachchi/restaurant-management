package com.restaurant.restaurant.services;

import com.restaurant.restaurant.models.MenuItem;
import com.restaurant.restaurant.models.Restaurant;
import com.restaurant.restaurant.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private RestaurantService restaurantService;

    public MenuItem addMenuItem(MenuItem item) {
        Restaurant restaurant = restaurantService.findByUserId(item.getUserId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (!restaurant.isVerifiedByAdmin()) {
            throw new RuntimeException("Restaurant is not verified by admin");
        }

        try {
            return menuItemRepository.save(item);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Menu name must be unique");
        }
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> allItems = menuItemRepository.findAll();
        return allItems.stream()
                .filter(item -> {
                    Optional<Restaurant> rest = restaurantService.findByUserId(item.getUserId());
                    return rest.isPresent() && rest.get().isVerifiedByAdmin();
                })
                .toList();
    }

    public Optional<MenuItem> getMenuItemById(String id) {
        return menuItemRepository.findById(id);
    }

    public List<MenuItem> getMenuItemsByRestaurantId(String restaurantId) {
        return menuItemRepository.findByRestaurantId(restaurantId);
    }

    public List<MenuItem> getMenuItemsByUserId(String userId) {
        Restaurant restaurant = restaurantService.findByUserId(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (!restaurant.isVerifiedByAdmin()) {
            throw new RuntimeException("Restaurant is not verified by admin");
        }

        return menuItemRepository.findByUserId(Long.parseLong(userId));
    }

    public MenuItem updateMenuItem(String id, Map<String, Object> updates) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        Restaurant restaurant = restaurantService.findByUserId(menuItem.getUserId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (!restaurant.isVerifiedByAdmin()) {
            throw new RuntimeException("Restaurant is not verified by admin");
        }

        updates.forEach((key, value) -> {
            switch (key) {
                case "userId" -> menuItem.setUserId((Long) value);
                case "name" -> menuItem.setName((String) value);
                case "price" -> menuItem.setPrice((Double) value);
                case "description" -> menuItem.setDescription((String) value);
                case "available" -> menuItem.setAvailable((Boolean) value);
            }
        });

        return menuItemRepository.save(menuItem);
    }

    public void deleteMenuItem(String id) {
        menuItemRepository.deleteById(id);
    }
}
