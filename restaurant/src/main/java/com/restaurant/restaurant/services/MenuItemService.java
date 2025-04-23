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

    public MenuItem addMenuItem(MenuItem item) {
        try {
            return menuItemRepository.save(item);
        } catch (DuplicateKeyException e) {
            throw new RuntimeException("Menu name must be unique");
        }
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public Optional<MenuItem> getMenuItemById(String id) {
        return menuItemRepository.findById(id);
    }

    public List<MenuItem> getMenuItemsByUserId(String userId) {
        return menuItemRepository.findByUserId(Long.parseLong(userId));
    }

    public MenuItem updateMenuItem(String id, Map<String, Object> updates) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

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
