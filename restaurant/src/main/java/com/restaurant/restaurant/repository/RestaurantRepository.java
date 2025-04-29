package com.restaurant.restaurant.repository;

import com.restaurant.restaurant.models.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    List<Restaurant> findByUserId(Long userId);
    Optional<Restaurant> findByName(String name);
}