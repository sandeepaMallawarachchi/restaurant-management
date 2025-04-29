package com.example.delivery.repository;

import com.example.delivery.models.DeliveryPerson;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

// DeliveryRepository.java updates:
public interface DeliveryRepository extends MongoRepository<DeliveryPerson, String> {
    List<DeliveryPerson> findByRegisteredRestaurantIdAndAvailableTrue(String restaurantId);
    List<DeliveryPerson> findByRegisteredRestaurantId(String restaurantId); // Add this new method
}