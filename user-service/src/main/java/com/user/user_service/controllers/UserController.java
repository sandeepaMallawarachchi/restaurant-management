package com.user.user_service.controllers;

import com.user.user_service.dto.response.RestaurantOwnerResponse;
import com.user.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/set-restaurant")
    @PreAuthorize("hasRole('ROLE_RESTAURANT_OWNER')")
    public ResponseEntity<RestaurantOwnerResponse> setRestaurant(
            @RequestAttribute(value = "userId") Long userId,
            @RequestParam Long restaurantId
    )
    {
        return ResponseEntity.ok(userService.setRestaurantId(restaurantId, userId));
    }

}
