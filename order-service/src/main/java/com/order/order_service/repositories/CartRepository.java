package com.order.order_service.repositories;

import com.order.order_service.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    boolean existsByUserId(Long userId);

    Cart getByUserId(Long userId);
}
