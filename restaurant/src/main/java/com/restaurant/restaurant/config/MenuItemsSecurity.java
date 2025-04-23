package com.restaurant.restaurant.config;

import com.restaurant.restaurant.models.MenuItem;
import com.restaurant.restaurant.repository.MenuItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("menuItemsSecurity")
public class MenuItemsSecurity {

    private final MenuItemRepository menuItemRepository;

    public MenuItemsSecurity(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public boolean isOwnerOrAdminByMenuItemId(Authentication authentication, String menuItemId) {
        if (authentication == null || menuItemId == null) return false;

        // Admins allowed
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        // Get the item
        Optional<MenuItem> optionalItem = menuItemRepository.findById(menuItemId);
        if (optionalItem.isEmpty()) return false;

        MenuItem item = optionalItem.get();

        try {
            Long currentUserId = Long.parseLong(authentication.getName());
            return item.getUserId().equals(currentUserId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
