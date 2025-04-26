package com.user.user_service.controllers;

import com.user.user_service.dto.response.RestaurantOwnerResponse;
import com.user.user_service.models.User;
import com.user.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    ) {
        return ResponseEntity.ok(userService.setRestaurantId(restaurantId, userId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestAttribute(value = "userId") Long userId,
            @RequestParam String password) {
        userService.changePassword(userId, password);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<User> getUserById(@RequestAttribute(value = "userId") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/admin-get-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<User> getUserByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Iterable<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
