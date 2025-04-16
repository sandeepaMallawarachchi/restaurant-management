package com.restaurant.restaurant.services;

import com.restaurant.restaurant.models.Restaurant;
import com.restaurant.restaurant.repository.RestaurantRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String authenticate(String name, String password) throws Exception {
        Optional<Restaurant> restaurantOpt = restaurantRepository.findByName(name);
        if (restaurantOpt.isPresent() && passwordEncoder.matches(password, restaurantOpt.get().getPassword())) {
            return generateToken(restaurantOpt.get());
        } else {
            throw new Exception("Invalid credentials");
        }
    }

    private String generateToken(Restaurant restaurant) {
        long expirationTime = 1000 * 60 * 60 * 24; // 24 hours
        return Jwts.builder()
                .setSubject(restaurant.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS256, "secret") // use a proper secret key
                .compact();
    }
}

