package com.order.order_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  Long restaurantId;

    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    private PaymentMethod paymentMethod;

    private Double orderTotal;

    private Double discount;

    private Double deliveryFee;

    private Double finalPrice;

    private OrderStatus orderStatus =  OrderStatus.PENDING;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    private String deliveryAddress;

    private String phoneNumber;

    private String email;

    private String deliveryUserName;


    private LocalDateTime estimatedDeliveryTime;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
