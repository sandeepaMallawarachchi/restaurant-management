package com.restaurant.restaurant.controllers;

import com.restaurant.restaurant.models.MenuItem;
import com.restaurant.restaurant.models.Restaurant;
import com.restaurant.restaurant.services.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/menu")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @PreAuthorize("hasRole('ADMIN') or #items[0].userId.toString() == authentication.name")
    @PostMapping
    public ResponseEntity<?> addMenuItems(@Valid @RequestBody List<MenuItem> items) {
        try {
            menuItemService.addMenuItem(items);
            return ResponseEntity.ok(Map.of("message", "Menu items added successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable String id) {
        Optional<MenuItem> item = menuItemService.getMenuItemById(id);
        return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByUserId(@PathVariable String userId) {
        List<MenuItem> items = menuItemService.getMenuItemsByUserId(userId);
        if (items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(items);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByRestaurantId(@PathVariable String restaurantId) {
        List<MenuItem> items = menuItemService.getMenuItemsByRestaurantId(restaurantId);
        if (items.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(items);
    }

    @PreAuthorize("@menuItemsSecurity.isOwnerOrAdminByMenuItemId(authentication, #id)")
    @PatchMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        MenuItem updatedMenu = menuItemService.updateMenuItem(id, updates);
        return ResponseEntity.ok(updatedMenu);
    }

    @PreAuthorize("@menuItemsSecurity.isOwnerOrAdminByMenuItemId(authentication, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable String id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok("Menu item deleted successfully");
    }
}
