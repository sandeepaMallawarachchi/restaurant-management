package com.order.order_service.repositories;

import com.order.order_service.models.Order;
import com.order.order_service.models.OrderStatus;
import com.order.order_service.models.PaymentMethod;
import com.order.order_service.models.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository  extends JpaRepository<Order,Long> {
    @Query("SELECT o FROM Order o WHERE " +
            "(:userId IS NULL OR o.userId = :userId) AND " +
            "(:orderStatus IS NULL OR o.orderStatus = :orderStatus) AND " +
            "(:restaurantId IS NULL OR o.restaurantId = :restaurantId) AND " +
            "(:orderDateStart IS NULL OR o.createdDate >= :orderDateStart) AND " +
            "(:orderDateEnd IS NULL OR o.createdDate <= :orderDateEnd) AND " +
            "(:paymentMethod IS NULL OR o.paymentMethod = :paymentMethod) AND " +
            "(:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus) AND " +
            "(:deliverBy IS NULL OR o.deliverBy = :deliverBy)")
    Page<Order> filterAll(
            @Param("userId") Long userId,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("restaurantId") Long restaurantId,
            @Param("orderDateStart") LocalDateTime orderDateStart,
            @Param("orderDateEnd") LocalDateTime orderDateEnd,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("deliverBy") Long deliverBy,
            Pageable pageable
    );

    @Query("SELECT o FROM Order o " +
            "WHERE o.restaurantId = :restaurantId AND " +
            "o.createdDate BETWEEN :startDate AND :endDate")
    List<Order> getByRestaurantIdAndDateBetween(
            Long restaurantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Order> findByDeliverBy(Long deliverBy);
}
