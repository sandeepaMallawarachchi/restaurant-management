package com.order.order_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Restaurant ID is required")
    private  Long restaurantId;

    @NotNull(message = "User ID is required")
    private Long userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    @NotNull(message = "Order total is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Order total must be at least 0")
    private Double orderTotal;

    @DecimalMin(value = "0.0", inclusive = true, message = "Discount must be at least 0")
    private Double discount;

    @DecimalMin(value = "0.0", inclusive = true, message = "Delivery fee must be at least 0")
    private Double deliveryFee;

    @NotNull(message = "Final price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Final price must be at least 0")
    private Double finalPrice;

    private OrderStatus orderStatus =  OrderStatus.PENDING;

    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @NotBlank(message = "Delivery address is required")
    private String deliveryAddress;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Delivery user name is required")
    private String deliveryUserName;

    private LocalDateTime estimatedDeliveryTime;

    private String specialNote;

    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
