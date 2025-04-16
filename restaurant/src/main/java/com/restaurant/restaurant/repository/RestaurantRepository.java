package com.restaurant.restaurant.repository;

import com.restaurant.restaurant.models.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    Optional<Restaurant> findByOwnerId(String ownerId);
    Optional<Restaurant> findByName(String name);
}