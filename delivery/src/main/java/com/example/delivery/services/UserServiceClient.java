package com.example.delivery.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class UserServiceClient {

    @Value("${user.service.url}")
    private String userServiceUrl;

    private final HttpServletRequest request;

    public UserServiceClient(HttpServletRequest request) {
        this.request = request;
    }

    public String getCityForCurrentUser() {
        RestTemplate restTemplate = new RestTemplate();

        String token = request.getHeader("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                userServiceUrl + "/user",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map<String, Object> userData = response.getBody();
        if (userData != null && userData.containsKey("city")) {
            return (String) userData.get("city");
        } else {
            throw new RuntimeException("City not found in user service response");
        }
    }
}
