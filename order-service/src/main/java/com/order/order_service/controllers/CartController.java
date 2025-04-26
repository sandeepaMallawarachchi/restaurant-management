package com.order.order_service.controllers;

import com.order.order_service.dto.requests.CartItemCreateRequest;
import com.order.order_service.dto.responses.CartResponse;
import com.order.order_service.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> addItemToCart(
            @RequestAttribute("userId") Long userId,
            @RequestBody CartItemCreateRequest request)
    {
        return ResponseEntity.ok(cartService.addToCart(request, userId));
    }

    @PutMapping("/remove-item")
    public ResponseEntity<CartResponse> removeItem(
            @RequestAttribute("userId") Long userId,
            @RequestBody CartItemCreateRequest request
    ){
        return ResponseEntity.ok(cartService.removeCartItem(request, userId));
    }

    @PutMapping("/decrease-item-quantity")
    public ResponseEntity<CartResponse> decreaseItemQuantity(
            @RequestAttribute("userId") Long userId,
            @RequestBody CartItemCreateRequest request
    ){
        return ResponseEntity.ok(cartService.decreaseCartItemQuantity(request, userId));
    }

    @PutMapping("/empty/{id}")
    public ResponseEntity<Void> emptyCart(@RequestAttribute("userId") Long userId,
                                            @PathVariable("id") Long cartId) {
        cartService.emptyCart(cartId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCartByUserId(
            @RequestAttribute("userId") Long userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCartByUserId(@RequestAttribute("userId") Long userId) {
        cartService.deleteCartByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
