package com.order.order_service.dto.requests;

import com.order.order_service.models.OrderStatus;
import com.order.order_service.models.PaymentMethod;
import com.order.order_service.models.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderFilterRequest {
    Long userId;
    OrderStatus orderStatus;
    Long restaurantId;
    LocalDateTime orderDateStart;
    LocalDateTime orderDateEnd;
    PaymentMethod paymentMethod;
    PaymentStatus paymentStatus;
    Integer page;
    Integer size;
}
