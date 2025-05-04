package com.example.delivery.controllers;

import com.example.delivery.config.JwtUtil;
import com.example.delivery.models.DeliveryPerson;
import com.example.delivery.models.RestaurantDTO;
import com.example.delivery.services.DeliveryService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/restaurants/city/{city}")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByCity(@PathVariable String city) {
        List<RestaurantDTO> restaurants = deliveryService.getAvailableRestaurantsByCity(city);
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerToRestaurant(@RequestBody Map<String, String> payload) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = jwtUtil.getAllClaims(token);
        String deliveryPersonId = String.valueOf(claims.get("userId"));
        String restaurantId = payload.get("restaurantId");

        deliveryService.registerToRestaurant(deliveryPersonId, restaurantId);
        return ResponseEntity.ok("Registered successfully.");
    }

    @GetMapping("/restaurants/user")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByUser() {
        List<RestaurantDTO> restaurants = deliveryService.getRestaurantsByUser();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/registered-restaurant")
    public ResponseEntity<Map<String, String>> getRegisteredRestaurantId() {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Claims claims = jwtUtil.getAllClaims(token);
        String deliveryPersonId = String.valueOf(claims.get("userId"));

        String registeredId = deliveryService.getRegisteredRestaurantId(deliveryPersonId);

        Map<String, String> response = new HashMap<>();
        response.put("restaurantId", registeredId != null ? registeredId : "");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    public ResponseEntity<String> updateDeliveryStatus(@RequestBody Map<String, String> payload) {
        String deliveryPersonId = payload.get("deliveryPersonId");
        String orderId = payload.get("orderId");
        String status = payload.get("status");

        deliveryService.updateDeliveryStatus(deliveryPersonId, orderId, status);
        return ResponseEntity.ok("Status updated successfully");
    }

    @PostMapping("/assign-delivery")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    public ResponseEntity<String> assignDelivery(@RequestBody Map<String, String> payload) {
        String restaurantId = payload.get("restaurantId");
        String orderId = payload.get("orderId");
        String assigned = deliveryService.assignDeliveryPersonToOrder(restaurantId, orderId);
        if (assigned != null) return ResponseEntity.ok("Assigned to DeliveryPerson ID: " + assigned);
        return ResponseEntity.badRequest().body("No available delivery person found.");
    }

    @GetMapping("/assigned-orders/user")
    @PreAuthorize("hasAuthority('ROLE_DELIVERY_PERSON')")
    public ResponseEntity<List<String>> getAssignedOrdersForDeliveryPerson() {
        String token = request.getHeader("Authorization").substring(7);
        Claims claims = jwtUtil.getAllClaims(token);
        String userId = String.valueOf(claims.get("userId"));
        List<String> orders = deliveryService.getAssignedOrdersByDeliveryPerson(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/assigned-orders/restaurant/{restaurantId}")
    public ResponseEntity<List<String>> getAssignedOrdersByRestaurant(@PathVariable String restaurantId) {
        List<String> orders = deliveryService.getAssignedOrdersByRestaurant(restaurantId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/all-delivery-persons")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_RESTAURANT_OWNER')")
    public ResponseEntity<List<DeliveryPerson>> getAllDeliveryPersons() {
        List<DeliveryPerson> list = deliveryService.getAllDeliveryPersons();
        return ResponseEntity.ok(list);
    }
}
