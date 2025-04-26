package com.order.order_service.services;

import com.order.order_service.dto.requests.CartCreateRequest;
import com.order.order_service.dto.requests.CartItemCreateRequest;
import com.order.order_service.dto.responses.CartItemResponse;
import com.order.order_service.dto.responses.CartResponse;
import com.order.order_service.exception.InvalidInputException;
import com.order.order_service.exception.ResourceNotFoundException;
import com.order.order_service.models.Cart;
import com.order.order_service.models.CartItem;
import com.order.order_service.repositories.CartItemRepository;
import com.order.order_service.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;


    public CartResponse addToCart(CartItemCreateRequest item, Long userId) {
        validateUserId(userId);
        validateRestaurantId(item.getRestaurantId());
        validateProductId(item.getProductId());

        CartResponse response;
        if (cartRepository.existsByUserId(userId)) {
            Cart cart = cartRepository.getByUserId(userId);
            response = updateCart(item, cart, userId);
        } else {
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

    private CartResponse updateCart(CartItemCreateRequest item, Cart cart, Long userId) {
        if (!Objects.equals(cart.getRestaurantId(), item.getRestaurantId())) {
            emptyCart(cart.getId(), userId);
            cart.setRestaurantId(item.getRestaurantId());
            cart.setTotal(item.getPrice());
            cart.setCartItems(List.of(toCartItem(item, cart)));
            cartRepository.save(cart);
        }

        boolean itemExists = cart.getCartItems().stream()
                .anyMatch(cartItem -> Objects.equals(cartItem.getProductId(), item.getProductId()));

        if (itemExists) {
            CartItem cartItem = cart.getCartItems().stream()
                    .filter(cartItem1 -> Objects.equals(cartItem1.getProductId(), item.getProductId()))
                    .findFirst()
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Cart item not found")
                    );

            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setSubtotal(cartItem.getPrice() * cartItem.getQuantity());
            cart.setTotal(cart.getTotal() + item.getPrice());
            cartRepository.save(cart);
        } else {
            CartItem cartItem = toCartItem(item, cart);
            cart.getCartItems().add(cartItem);
            cart.setTotal(cart.getTotal() + item.getPrice());
            cartRepository.save(cart);
        }
        return mapToCartResponse(cart);
    }

    public CartResponse removeCartItem(CartItemCreateRequest request, Long userId) {
        validateUserId(userId);
        validateProductId(request.getProductId());
        validateRestaurantId(request.getRestaurantId());

        Cart cart = cartRepository.getByUserId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        if (!Objects.equals(cart.getRestaurantId(), request.getRestaurantId())) {
            throw new InvalidInputException(
                    "Restaurant ID does not match with the cart's restaurant ID");
        }
        if (cart.getCartItems().isEmpty()) {
            throw new InvalidInputException("Cart is already empty");
        }

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> Objects.equals(cartItem.getProductId(), request.getProductId()))
                .findFirst()
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cart item not found")
                );

        cart.getCartItems().remove(item);
        cart.setTotal(cart.getTotal() - item.getSubtotal());
        cartItemRepository.delete(item);
        cartRepository.save(cart);
        return mapToCartResponse(cart);
    }

    public CartResponse decreaseCartItemQuantity(CartItemCreateRequest request, Long userId) {
        validateUserId(userId);
        validateProductId(request.getProductId());
        validateRestaurantId(request.getRestaurantId());

        Cart cart = cartRepository.getByUserId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        if (!Objects.equals(cart.getRestaurantId(), request.getRestaurantId())) {
            throw new InvalidInputException(
                    "Restaurant ID does not match with the cart's restaurant ID");
        }
        if (cart.getCartItems().isEmpty()) {
            throw new InvalidInputException("Cart is empty");
        }

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> Objects.equals(cartItem.getProductId(), request.getProductId()))
                .findFirst()
                .orElseThrow(
                        () -> new ResourceNotFoundException("Cart item not found")
                );
        if (item.getQuantity() == 1) {
            cart.getCartItems().remove(item);
            cart.setTotal(cart.getTotal() - item.getSubtotal());
            cartRepository.save(cart);
        } else {
            item.setQuantity(item.getQuantity() - 1);
            item.setSubtotal(item.getPrice() * item.getQuantity());
            cart.setTotal(cart.getTotal() - item.getPrice());
            cartRepository.save(cart);
        }
        return mapToCartResponse(cart);
    }

    public void emptyCart(Long cartId, Long userId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new ResourceNotFoundException("Cart not found")
        );
        if (!Objects.equals(cart.getUserId(), userId)) {
            throw new InvalidInputException("User ID does not match with the cart's user ID");
        }
        cart.getCartItems().clear();
        cartRepository.save(cart);
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

    private CartItem toCartItem(CartItemCreateRequest cartItem, Cart cart) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProductId(cartItem.getProductId());
        item.setProductName(cartItem.getProductName());
        item.setQuantity(1);
        item.setPrice(cartItem.getPrice());
        item.setSubtotal(cartItem.getPrice());
        return item;
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

    public CartResponse getCartByUserId(Long userId) {
        Cart cart = cartRepository.getByUserId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        return mapToCartResponse(cart);
    }

    public void deleteCartByUserId(Long userId) {
        Cart cart = cartRepository.getByUserId(userId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart not found");
        }
        cartRepository.delete(cart);
    }


}
