package com.restaurant.restaurant.config;

import com.restaurant.restaurant.repository.RestaurantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("menuItemsSecurity")
public class MenuItemsSecurity {

    private final RestaurantRepository restaurantRepository;

    public MenuItemsSecurity(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public boolean isOwnerOrAdmin(Authentication auth, Long userId) {
        if (userId == null) return false;

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        try {
            Long authenticatedUserId = Long.valueOf(auth.getName());
            return userId.equals(authenticatedUserId);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
