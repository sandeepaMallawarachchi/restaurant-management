package com.restaurant.restaurant.services;

import com.restaurant.restaurant.models.MenuItem;
import com.restaurant.restaurant.models.Restaurant;
import com.restaurant.restaurant.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public MenuItem updateMenuItem(String id, MenuItem item) {
        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MenuItem not found"));
        if (item.getName() != null) existing.setName(item.getName());
        if (item.getPrice() != null) existing.setPrice(item.getPrice());
        if (item.getDescription() != null) existing.setDescription(item.getDescription());
        existing.setAvailable(item.isAvailable());
        return menuItemRepository.save(existing);
    }

    public void deleteMenuItem(String id) {
        menuItemRepository.deleteById(id);
    }
}
