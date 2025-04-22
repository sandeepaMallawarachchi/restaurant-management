package com.restaurant.restaurant.repository;

import com.restaurant.restaurant.models.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    Optional<Restaurant> findByEmail(String email);
    Optional<Restaurant> findByName(String name);
}