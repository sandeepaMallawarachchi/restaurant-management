package com.user.user_service.services;

import com.user.user_service.dto.response.DeliveryPersonSignupResponse;
import com.user.user_service.dto.response.RestaurantOwnerResponse;
import com.user.user_service.exception.ResourceNotFoundException;
import com.user.user_service.models.User;
import com.user.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SignupService signupService;


    //    @Cacheable(value = "userCache", key = "#id")
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + id)
        );
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
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

    public void changePassword(Long userId, String password) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );

        user.setPassword(encoder.encode(password));
        userRepository.save(user);
    }


    public DeliveryPersonSignupResponse verifyDeliveryPerson(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );
        if(!user.getRoles().contains("ROLE_DELIVERY_PERSON") ) {
            throw new ResourceNotFoundException("User is not a delivery person");
        }

        user.setVerified(true);
        user.setAvailability(true);
        userRepository.save(user);
        return signupService.mapToDeliveryPersonSignupResponse(user, user.getDeliveryVehicle());
    }

    public void unverifyDeliveryPerson(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );
        if(!user.getRoles().contains("ROLE_DELIVERY_PERSON") ) {
            throw new ResourceNotFoundException("User is not a delivery person");
        }

        user.setVerified(false);
        user.setAvailability(false);
        userRepository.save(user);
    }

    public List<DeliveryPersonSignupResponse> getAvailableDeliveryPersons() {
        List<User> availableUsers = new ArrayList<>();
        userRepository.findAll().forEach(user -> {
            if(user.getRoles().contains("ROLE_DELIVERY_PERSON") && user.isAvailability() && user.isVerified()) {
                availableUsers.add(user);
            }
        });
        return availableUsers.stream()
                .map(user -> signupService.mapToDeliveryPersonSignupResponse(
                        user, user.getDeliveryVehicle()
                ))
                .toList();
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
