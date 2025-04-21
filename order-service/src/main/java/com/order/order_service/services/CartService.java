package com.order.order_service.services;

import com.order.order_service.dto.requests.CartCreateRequest;
import com.order.order_service.dto.requests.CartItemCreateRequest;
import com.order.order_service.dto.responses.CartResponse;
import com.order.order_service.models.Cart;

public interface CartService {

    CartResponse addToCart(CartItemCreateRequest request, Long userId);
    CartResponse removeCartItem(CartItemCreateRequest request);
    CartResponse decreaseCartItemQuantity(CartItemCreateRequest request);
    void emptyCart(Cart cart);
}
