package com.order.order_service.services.serviceImpl;

import com.order.order_service.dto.requests.CartCreateRequest;
import com.order.order_service.dto.requests.CartItemCreateRequest;
import com.order.order_service.dto.responses.CartItemResponse;
import com.order.order_service.dto.responses.CartResponse;
import com.order.order_service.models.Cart;
import com.order.order_service.models.CartItem;
import com.order.order_service.repositories.CartRepository;
import com.order.order_service.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;


    @Override
    public CartResponse addToCart(CartItemCreateRequest item, Long userId) {
        validateUserId(userId);
        validateRestaurantId(item.getRestaurantId());
        validateProductId(item.getProductId());

        CartResponse response;
        if(cartRepository.existsByUserId(userId)){
            Cart cart = cartRepository.getByUserId(userId);
            response = updateCart(item, cart);
        }
        else{
            CartCreateRequest cartCreateRequest = new CartCreateRequest();
            cartCreateRequest.setUserId(userId);
            cartCreateRequest.setCartItem(item);
            cartCreateRequest.setRestaurantId(item.getRestaurantId());
            response = createCart(cartCreateRequest);
        }
        return response;
    }

    private CartResponse createCart(CartCreateRequest cartCreateRequest) {
        Cart cart = new Cart();
        cart.setUserId(cartCreateRequest.getUserId());
        cart.setRestaurantId(cartCreateRequest.getRestaurantId());
        cart.setTotal(cartCreateRequest.getCartItem().getPrice());

        List<CartItem> itemList = new ArrayList<>();
        itemList.add(toCartItem(cartCreateRequest.getCartItem(), cart));
        cart.setCartItems(itemList);
        cartRepository.save(cart);

        return mapToCartResponse(cart);
    }

    private CartItem toCartItem(CartItemCreateRequest cartItem,  Cart cart) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(cartItem.getProductId());
        item.setProductName(cartItem.getProductName());
        item.setQuantity(1);
        item.setPrice(cartItem.getPrice());
        item.setSubtotal(cartItem.getPrice());
        return item;
    }

    private CartResponse updateCart(CartItemCreateRequest item,  Cart cart) {
        return null;
    }

    @Override
    public CartResponse removeCartItem(CartItemCreateRequest request) {
        return null;
    }

    @Override
    public CartResponse decreaseCartItemQuantity(CartItemCreateRequest request) {
        return null;
    }

    @Override
    public void emptyCart(Cart cart) {

    }


    private CartResponse mapToCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .restaurantId(cart.getRestaurantId())
                .total(cart.getTotal())
                .cartItems(cart.getCartItems().stream()
                        .map(this::mapToCartItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .build();
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("user id must not be null");
        }
    }

    private void validateRestaurantId(Long restaurantId) {
        if (restaurantId == null) {
            throw new RuntimeException("restaurant id must not be null");
        }
    }

    private void validateProductId(Long productId) {
        if (productId == null) {
            throw new RuntimeException("product id must not be null");
        }
    }


}
