package com.example.delivery.services;

import com.example.delivery.models.RestaurantDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantServiceClient {

    @Value("${restaurant.service.url}")
    private String restaurantServiceUrl;

    public List<RestaurantDTO> getAvailableRestaurantsByCity(String city) {
        RestTemplate restTemplate = new RestTemplate();
        RestaurantDTO[] all = restTemplate.getForObject(restaurantServiceUrl + "/restaurants", RestaurantDTO[].class);
        if (all == null) return List.of();
        return Arrays.stream(all)
                .filter(r -> r.getCity().equalsIgnoreCase(city) && r.isAvailable() && r.isVerifiedByAdmin())
                .collect(Collectors.toList());
    }
}
