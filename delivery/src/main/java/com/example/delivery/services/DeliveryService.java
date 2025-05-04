package com.example.delivery.services;

import com.example.delivery.models.DeliveryPerson;
import com.example.delivery.models.RestaurantDTO;
import com.example.delivery.repository.DeliveryPersonRepository;
import com.example.delivery.repository.DeliveryRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersonRepository deliveryPersonRepository;
    private final HttpServletRequest request;
    private final UserServiceClient userServiceClient;

    @Value("${restaurant.service.url}")
    private String restaurantServiceUrl;

    public DeliveryService(
            DeliveryRepository deliveryRepository,
            DeliveryPersonRepository deliveryPersonRepository,
            HttpServletRequest request,
            UserServiceClient userServiceClient
    ) {
        this.deliveryRepository = deliveryRepository;
        this.deliveryPersonRepository = deliveryPersonRepository;
        this.request = request;
        this.userServiceClient = userServiceClient;
    }

    public List<RestaurantDTO> getAvailableRestaurantsByCity(String city) {
        RestTemplate restTemplate = new RestTemplate();
        String token = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<RestaurantDTO[]> response = restTemplate.exchange(
                restaurantServiceUrl + "/restaurants",
                HttpMethod.GET,
                entity,
                RestaurantDTO[].class
        );

        RestaurantDTO[] all = response.getBody();
        if (all == null) return List.of();

        return Arrays.stream(all)
                .filter(r -> r.getCity().equalsIgnoreCase(city) && r.isAvailable() && r.isVerifiedByAdmin())
                .collect(Collectors.toList());
    }

    public void registerToRestaurant(String deliveryPersonId, String restaurantId) {
        DeliveryPerson person = deliveryPersonRepository.findById(deliveryPersonId).orElse(null);

        if (person == null) {
            person = new DeliveryPerson();
            person.setId(deliveryPersonId);
            person.setName("Mock Delivery Person");
        }

        person.setRegisteredRestaurantId(restaurantId);
        deliveryPersonRepository.save(person);
    }

    public List<RestaurantDTO> getRestaurantsByUser() {
        String city = userServiceClient.getCityForCurrentUser();

        RestTemplate restTemplate = new RestTemplate();
        String token = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<RestaurantDTO[]> response = restTemplate.exchange(
                restaurantServiceUrl + "/restaurants",
                HttpMethod.GET,
                entity,
                RestaurantDTO[].class
        );

        RestaurantDTO[] all = response.getBody();
        if (all == null) return List.of();

        return Arrays.stream(all)
                .filter(r -> r.getCity().equalsIgnoreCase(city) && r.isAvailable() && r.isVerifiedByAdmin())
                .collect(Collectors.toList());
    }

    public void updateDeliveryStatus(String deliveryPersonId, String orderId, String status) {
        DeliveryPerson person = deliveryPersonRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        person.getAssignedOrderIds().remove(orderId);

        if (person.getAssignedOrderIds().isEmpty()) {
            person.setAvailable(true);
        }

        person.setDeliveryStatus(status);
        deliveryPersonRepository.save(person);
    }

    public List<String> getAssignedOrdersByDeliveryPerson(String deliveryPersonId) {
        return deliveryPersonRepository.findById(deliveryPersonId)
                .map(DeliveryPerson::getAssignedOrderIds)
                .orElse(List.of());
    }

    public List<String> getAssignedOrdersByRestaurant(String restaurantId) {
        return deliveryPersonRepository.findByRegisteredRestaurantId(restaurantId).stream()
                .flatMap(person -> person.getAssignedOrderIds().stream())
                .collect(Collectors.toList());
    }

    public List<DeliveryPerson> getAllDeliveryPersons() {
        return deliveryPersonRepository.findAll();
    }

    public String assignDeliveryPersonToOrder(String restaurantId, String orderId) {
        List<DeliveryPerson> candidates = deliveryPersonRepository.findByRegisteredRestaurantIdAndAvailableTrue(restaurantId);

        if (candidates.isEmpty()) {
            return null;
        }

        DeliveryPerson person = candidates.get(0);
        person.getAssignedOrderIds().add(orderId);
        person.setAvailable(false);
        person.setDeliveryStatus("DELIVERING");

        deliveryPersonRepository.save(person);
        return person.getId();
    }

    public String getRegisteredRestaurantId(String deliveryPersonId) {
        DeliveryPerson dp = deliveryPersonRepository.findById(deliveryPersonId).orElse(null);
        return dp != null ? dp.getRegisteredRestaurantId() : null;
    }
}