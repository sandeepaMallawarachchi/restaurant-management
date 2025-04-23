package com.restaurant.restaurant.repository;

import com.restaurant.restaurant.models.MenuItem;
import com.restaurant.restaurant.models.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends MongoRepository<MenuItem, String> {
    List<MenuItem> findByUserId(Long userId);
}
