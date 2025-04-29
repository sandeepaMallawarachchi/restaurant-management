package com.example.delivery.repository;

import com.example.delivery.models.DeliveryPerson;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DeliveryPersonRepository extends MongoRepository<DeliveryPerson, String> {
    List<DeliveryPerson> findByCity(String city);
}
