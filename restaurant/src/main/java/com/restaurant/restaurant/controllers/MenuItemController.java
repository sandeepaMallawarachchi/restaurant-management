package com.restaurant.restaurant.controllers;

import com.restaurant.restaurant.models.MenuItem;
import com.restaurant.restaurant.services.MenuItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/menu")
public class MenuItemController {

    @Autowired
    private MenuItemService menuItemService;

    @PreAuthorize("@restaurantSecurity.isAdminOrRestaurantOwner()")
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@Valid @RequestBody MenuItem item) {
        return ResponseEntity.ok(menuItemService.addMenuItem(item));
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

    @PreAuthorize("@restaurantSecurity.isAdminOrRestaurantOwner()")
    @PatchMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(@PathVariable String id, @RequestBody MenuItem item) {
        return ResponseEntity.ok(menuItemService.updateMenuItem(id, item));
    }

    @PreAuthorize("@restaurantSecurity.isAdminOrRestaurantOwner()")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable String id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.ok("Menu item deleted successfully");
    }
}
