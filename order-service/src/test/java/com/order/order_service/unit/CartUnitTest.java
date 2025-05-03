package com.order.order_service.unit;

import com.order.order_service.dto.requests.CartItemCreateRequest;
import com.order.order_service.dto.responses.CartItemResponse;
import com.order.order_service.dto.responses.CartResponse;
import com.order.order_service.models.Cart;
import com.order.order_service.models.CartItem;
import com.order.order_service.repositories.CartRepository;
import com.order.order_service.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartUnitTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    // Test data
    private static final Long USER_ID_1 = 1L;
    private static final Long USER_ID_2 = 2L;
    private static final String RESTAURANT_ID_1 = "1L";
    private static final String RESTAURANT_ID_2 = "2L";
    private static final String PRODUCT_ID_1 = "1L";
    private static final String PRODUCT_ID_2 = "2L";
    private static final String PRODUCT_ID_3 = "3L";
    private static final String PRODUCT_NAME_1 = "Burger";
    private static final String PRODUCT_NAME_2 = "Sandwich";
    private static final double PRICE_1 = 100.0;
    private static final double PRICE_2 = 200.0;

    private CartItemCreateRequest itemRequest;
    private Cart existingCart;

    @BeforeEach
    public void setUp() {
        // Initialize item request
        itemRequest = createCartItemRequest(PRODUCT_ID_1, PRODUCT_NAME_1, PRICE_1, RESTAURANT_ID_1);

        // Initialize existing cart for user 2
        existingCart = new Cart();
        existingCart.setId(1L);
        existingCart.setUserId(USER_ID_2);
        existingCart.setRestaurantId(RESTAURANT_ID_1);
        existingCart.setCreatedDate(LocalDateTime.now());
        existingCart.setTotal(0.0);
        existingCart.setCartItems(new ArrayList<>());
    }

    @Test
    public void testAddToCart_userDoesNotHaveCart_shouldCreateCart() {
        // Arrange
        when(cartRepository.existsByUserId(USER_ID_1)).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setId(1L);

            Long cartItemId = 1L;
            for (CartItem item : savedCart.getCartItems()) {
                item.setId(cartItemId++);
            }
            return savedCart;
        });

        // Act
        CartResponse result = cartService.addToCart(itemRequest, USER_ID_1);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(USER_ID_1, result.getUserId());
        assertEquals(RESTAURANT_ID_1, result.getRestaurantId());
        assertEquals(PRICE_1, result.getTotal());

        // Verify cart items
        List<CartItemResponse> cartItems = new ArrayList<>(result.getCartItems());
        assertEquals(1, cartItems.size());

        CartItemResponse cartItem = cartItems.get(0);
        assertEquals(1L, cartItem.getId());
        assertEquals(PRODUCT_ID_1, cartItem.getProductId());
        assertEquals(PRODUCT_NAME_1, cartItem.getProductName());
        assertEquals(PRICE_1, cartItem.getPrice());
        assertEquals(1, cartItem.getQuantity());
        assertEquals(PRICE_1, cartItem.getSubtotal());

        // Verify repository interactions
        verify(cartRepository).existsByUserId(USER_ID_1);
        verify(cartRepository).save(any(Cart.class));
        verify(cartRepository, never()).getByUserId(anyLong());
    }

    @Test
    public void testAddToCart_userHasCart_shouldUpdateCart() {
        // Arrange
        when(cartRepository.existsByUserId(USER_ID_2)).thenReturn(true);
        when(cartRepository.getByUserId(USER_ID_2)).thenReturn(existingCart);
        when(cartRepository.save(existingCart)).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setLastModifiedDate(LocalDateTime.now());

            Long cartItemId = 3L;
            for (CartItem item : savedCart.getCartItems()) {
                if (item.getId() == null) {
                    item.setId(cartItemId++);
                }
            }
            return savedCart;
        });

        // Act
        CartResponse result = cartService.addToCart(itemRequest, USER_ID_2);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(USER_ID_2, result.getUserId());
        assertEquals(RESTAURANT_ID_1, result.getRestaurantId());
        assertEquals(PRICE_1, result.getTotal());

        // Verify cart items
        List<CartItemResponse> cartItems = new ArrayList<>(result.getCartItems());
        assertEquals(1, cartItems.size());

        CartItemResponse cartItem = cartItems.get(0);
        assertEquals(3L, cartItem.getId());
        assertEquals(PRODUCT_ID_1, cartItem.getProductId());
        assertEquals(PRODUCT_NAME_1, cartItem.getProductName());
        assertEquals(PRICE_1, cartItem.getPrice());
        assertEquals(1, cartItem.getQuantity());
        assertEquals(PRICE_1, cartItem.getSubtotal());

        // Verify repository interactions
        verify(cartRepository).existsByUserId(USER_ID_2);
        verify(cartRepository).save(existingCart);
        verify(cartRepository).getByUserId(USER_ID_2);
    }

    @Test
    public void testAddToCart_differentRestaurantId_shouldClearCartAndAddNewItem() {
        // Arrange
        existingCart.setRestaurantId(RESTAURANT_ID_2);

        // Add initial item with different restaurant
        CartItem initialItem = createCartItem(existingCart, PRODUCT_ID_3, PRODUCT_NAME_2, PRICE_2, 3);
        existingCart.getCartItems().add(initialItem);
        existingCart.setTotal(initialItem.getSubtotal());

        when(cartRepository.existsByUserId(USER_ID_2)).thenReturn(true);
        when(cartRepository.getByUserId(USER_ID_2)).thenReturn(existingCart);
        when(cartRepository.save(existingCart)).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setLastModifiedDate(LocalDateTime.now());

            Long cartItemId = 3L;
            for (CartItem item : savedCart.getCartItems()) {
                if (item.getId() == null) {
                    item.setId(cartItemId++);
                }
            }
            return savedCart;
        });

        // Act
        CartResponse result = cartService.addToCart(itemRequest, USER_ID_2);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RESTAURANT_ID_1, result.getRestaurantId()); // Restaurant ID should be updated
        assertEquals(PRICE_1, result.getTotal());

        // Verify cart item was replaced
        List<CartItemResponse> cartItems = new ArrayList<>(result.getCartItems());
        assertEquals(1, cartItems.size());

        CartItemResponse cartItem = cartItems.get(0);
        assertEquals(3L, cartItem.getId());
        assertEquals(PRODUCT_ID_1, cartItem.getProductId());
        assertEquals(PRODUCT_NAME_1, cartItem.getProductName());
        assertEquals(PRICE_1, cartItem.getPrice());
        assertEquals(1, cartItem.getQuantity());
        assertEquals(PRICE_1, cartItem.getSubtotal());

        // Verify repository interactions
        verify(cartRepository).existsByUserId(USER_ID_2);
        verify(cartRepository).save(existingCart);
        verify(cartRepository).getByUserId(USER_ID_2);
    }

    @Test
    public void testAddToCart_sameRestaurantDifferentProduct_shouldAddNewItem() {
        // Arrange
        CartItem existingItem = createCartItem(existingCart, PRODUCT_ID_3, PRODUCT_NAME_2, PRICE_2, 3);
        existingCart.getCartItems().add(existingItem);
        existingCart.setTotal(existingItem.getSubtotal());

        when(cartRepository.existsByUserId(USER_ID_2)).thenReturn(true);
        when(cartRepository.getByUserId(USER_ID_2)).thenReturn(existingCart);
        when(cartRepository.save(existingCart)).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setLastModifiedDate(LocalDateTime.now());

            Long cartItemId = 3L;
            for (CartItem item : savedCart.getCartItems()) {
                if (item.getId() == null) {
                    item.setId(cartItemId++);
                }
            }
            return savedCart;
        });

        // Act
        CartResponse result = cartService.addToCart(itemRequest, USER_ID_2);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RESTAURANT_ID_1, result.getRestaurantId());
        assertEquals(existingItem.getSubtotal() + PRICE_1, result.getTotal());

        // Verify two cart items exist
        List<CartItemResponse> cartItems = new ArrayList<>(result.getCartItems());
        assertEquals(2, cartItems.size());

        CartItemResponse firstItem = cartItems.get(0);
        assertEquals(3L, firstItem.getId());
        assertEquals(PRODUCT_ID_3, firstItem.getProductId());
        assertEquals(PRODUCT_NAME_2, firstItem.getProductName());
        assertEquals(PRICE_2, firstItem.getPrice());
        assertEquals(3, firstItem.getQuantity());
        assertEquals(PRICE_2 * 3, firstItem.getSubtotal());

        CartItemResponse secondItem = cartItems.get(1);
        assertEquals(4L, secondItem.getId());
        assertEquals(PRODUCT_ID_1, secondItem.getProductId());
        assertEquals(PRODUCT_NAME_1, secondItem.getProductName());
        assertEquals(PRICE_1, secondItem.getPrice());
        assertEquals(1, secondItem.getQuantity());
        assertEquals(PRICE_1, secondItem.getSubtotal());

        // Verify repository interactions
        verify(cartRepository).existsByUserId(USER_ID_2);
        verify(cartRepository).save(existingCart);
        verify(cartRepository).getByUserId(USER_ID_2);
    }

    @Test
    public void testAddToCart_sameRestaurantSameProduct_shouldIncrementQuantity() {
        //Arrange
        CartItem existingItem = createCartItem(existingCart, itemRequest.getProductId(), itemRequest.getProductName(),
                itemRequest.getPrice(), 3);
        existingCart.getCartItems().add(existingItem);
        existingCart.setTotal(existingItem.getSubtotal());

        when(cartRepository.existsByUserId(USER_ID_2)).thenReturn(true);
        when(cartRepository.getByUserId(USER_ID_2)).thenReturn(existingCart);
        when(cartRepository.save(existingCart)).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setLastModifiedDate(LocalDateTime.now());
            savedCart.setTotal(existingItem.getSubtotal() + itemRequest.getPrice());
            Long cartItemId = 3L;
            for (CartItem item : savedCart.getCartItems()) {
                if (item.getId() == null) {
                    item.setId(cartItemId++);
                }
            }
            return savedCart;
        });

        CartResponse result = cartService.addToCart(itemRequest, USER_ID_2);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RESTAURANT_ID_1, result.getRestaurantId());
        assertEquals(existingItem.getSubtotal() + itemRequest.getPrice(), result.getTotal());
        assertEquals(1, result.getCartItems().size());
        assertEquals(4,result.getCartItems().get(0).getQuantity());
        verify(cartRepository).existsByUserId(USER_ID_2);
        verify(cartRepository).save(existingCart);
        verify(cartRepository).getByUserId(USER_ID_2);
    }

    /**
     * Helper method to create a cart item request
     */
    private CartItemCreateRequest createCartItemRequest(String productId, String productName,
                                                        double price, String restaurantId) {
        CartItemCreateRequest request = new CartItemCreateRequest();
        request.setProductId(productId);
        request.setProductName(productName);
        request.setPrice(price);
        request.setRestaurantId(restaurantId);
        return request;
    }

    /**
     * Helper method to create a cart item
     */
    private CartItem createCartItem(Cart cart, String productId, String productName,
                                    double price, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProductId(productId);
        cartItem.setProductName(productName);
        cartItem.setPrice(price);
        cartItem.setQuantity(quantity);
        cartItem.setSubtotal(price * quantity);
        cartItem.setCreatedDate(LocalDateTime.now());
        return cartItem;
    }
}