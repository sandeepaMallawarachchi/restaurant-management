package com.user.user_service.services;

import com.user.user_service.dto.response.RestaurantOwnerResponse;
import com.user.user_service.exception.ResourceNotFoundException;
import com.user.user_service.models.User;
import com.user.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Cacheable(value = "userCache", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + id)
        );
    }

    public RestaurantOwnerResponse setRestaurantId(Long restaurantId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );

        Set<Long> restaurantIds = user.getRestaurantIds();
        restaurantIds.add(restaurantId);
        user.setRestaurantIds(restaurantIds);

        userRepository.save(user);

        return mapToRestaurantOwnerResponse(user);
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );

        userRepository.delete(user);
    }


    private RestaurantOwnerResponse mapToRestaurantOwnerResponse(User user) {
        return RestaurantOwnerResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .restaurantIds(user.getRestaurantIds())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .addresses(user.getAddress())
                .build();
    }
}
