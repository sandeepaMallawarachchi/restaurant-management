package com.restaurant.restaurant.config;

import com.restaurant.restaurant.models.Restaurant;
import com.restaurant.restaurant.repository.RestaurantRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("restaurantSecurity")
public class RestaurantSecurity {

    private final RestaurantRepository restaurantRepository;

    public RestaurantSecurity(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public boolean isOwnerOrAdmin(Authentication auth, String restaurantId) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        try {
            Long userId = Long.parseLong(auth.getName());
            return restaurantRepository.findById(restaurantId)
                    .map(r -> userId.equals(r.getUserId()))
                    .orElse(false);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
