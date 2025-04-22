package com.restaurant.restaurant.repository;

import com.restaurant.restaurant.models.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MenuItemRepository extends MongoRepository<MenuItem, String> {}
