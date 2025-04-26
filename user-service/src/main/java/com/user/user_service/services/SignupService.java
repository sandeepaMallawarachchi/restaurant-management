package com.user.user_service.services;

import com.user.user_service.dto.request.AdminSignupRequest;
import com.user.user_service.dto.request.DeliveryPersonSignupRequest;
import com.user.user_service.dto.request.RestaurantOwnerSignupRequest;
import com.user.user_service.dto.request.UserSignupRequest;
import com.user.user_service.dto.response.*;
import com.user.user_service.exception.DuplicateEntityException;
import com.user.user_service.exception.ResourceNotFoundException;
import com.user.user_service.models.DeliveryVehicle;
import com.user.user_service.models.DeliveryVehicleType;
import com.user.user_service.models.Role;
import com.user.user_service.models.User;
import com.user.user_service.repositories.RoleRepository;
import com.user.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SignupService {

    private static final Logger logger = LoggerFactory.getLogger(SignupService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;


    public AdminSignupResponse signupAdmin(AdminSignupRequest adminSignupRequest) {
        logger.info("Processing admin signup request for username: {}", adminSignupRequest.getUsername());
        checkAlreadyExists(adminSignupRequest.getUsername(), adminSignupRequest.getEmail());

        Role role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(
                () -> {
                    logger.error("ROLE_ADMIN not found in the database");
                    return new ResourceNotFoundException("Role not found");
                });

        User user = new User();
        user.setUsername(adminSignupRequest.getUsername());
        user.setPassword(encoder.encode(adminSignupRequest.getPassword()));
        user.setEmail(adminSignupRequest.getEmail());
        user.setFirstName(adminSignupRequest.getFirstName());
        user.setLastName(adminSignupRequest.getLastName());
        user.setRoles(role != null ? Collections.singleton(role) : Collections.emptySet());

        User savedUser = userRepository.save(user);
        logger.info("Admin user created successfully with ID: {}", savedUser.getId());
        return mapToAdminSignupResponse(savedUser);
    }

    public UserSignupResponse signupUser(UserSignupRequest request) {
        logger.info("Processing regular user signup request for username: {}", request.getUsername());
        checkAlreadyExists(request.getUsername(), request.getEmail());

        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(
                () -> {
                    logger.error("ROLE_USER not found in the database");
                    return new ResourceNotFoundException("Role not found");
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(role != null ? Collections.singleton(role) : Collections.emptySet());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPostalCode(request.getPostalCode());

        User savedUser = userRepository.save(user);
        logger.info("Regular user created successfully with ID: {}", savedUser.getId());
        return mapToUserSignupResponse(savedUser);
    }

    public RestaurantOwnerSignupResponse signupRestaurantOwner(RestaurantOwnerSignupRequest request) {
        logger.info("Processing restaurant owner signup request for username: {}", request.getUsername());
        checkAlreadyExists(request.getUsername(), request.getEmail());

        Role role = roleRepository.findByName("ROLE_RESTAURANT_OWNER").orElseThrow(
                () -> {
                    logger.error("ROLE_RESTAURANT_OWNER not found in the database");
                    return new ResourceNotFoundException("Role not found");
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(role != null ? Collections.singleton(role) : Collections.emptySet());
        user.setPhoneNumber(request.getPhoneNumber());

        User savedUser = userRepository.save(user);
        logger.info("Restaurant owner created successfully with ID: {}", savedUser.getId());
        return mapToRestaurantOwnerSignupResponse(savedUser);
    }

    public DeliveryPersonSignupResponse signupDeliveryPerson(DeliveryPersonSignupRequest request) {
        logger.info("Processing delivery person signup request for username: {}", request.getUsername());
        checkAlreadyExists(request.getUsername(), request.getEmail());

        Role role = roleRepository.findByName("ROLE_DELIVERY_PERSON").orElseThrow(
                () -> {
                    logger.error("ROLE_DELIVERY_PERSON not found in the database");
                    return new ResourceNotFoundException("Role not found");
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(role != null ? Collections.singleton(role) : Collections.emptySet());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setPostalCode(request.getPostalCode());
        user.setProfileImage(request.getProfileImg());

        logger.debug("Creating delivery vehicle for user: {}", request.getUsername());
        DeliveryVehicle vehicle = new DeliveryVehicle();
        try {
            vehicle.setVehicleNumber(request.getVehicleNumber());
            vehicle.setVehicleType(DeliveryVehicleType.valueOf(request.getVehicleType().toUpperCase()));
            vehicle.setDrivingLicense(request.getLicenseNumber());
            vehicle.setVehicleImg(request.getVehicleImg());
            vehicle.setVehicleDocuments(request.getVehicleDocuments());
            vehicle.setDeliveryPerson(user);
            user.setDeliveryVehicle(vehicle);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid vehicle type provided: {}", request.getVehicleType(), e);
            throw new IllegalArgumentException("Invalid vehicle type: " + request.getVehicleType());
        }

        User savedUser = userRepository.save(user);
        logger.info("Delivery person created successfully with ID: {}", savedUser.getId());
        return mapToDeliveryPersonSignupResponse(savedUser, savedUser.getDeliveryVehicle());
    }

    private void checkAlreadyExists(String username, String email) {
        logger.debug("Checking if username: {} or email: {} already exists", username, email);

        if (userNameExists(username)) {
            logger.warn("Username already exists: {}", username);
            throw new DuplicateEntityException("Username already exists");
        }

        if (emailExists(email)) {
            logger.warn("Email already exists: {}", email);
            throw new DuplicateEntityException("Email already exists");
        }

        logger.debug("Username and email validation passed");
    }

    private boolean userNameExists(String userName) {
        User user = userRepository.findByUsername(userName).orElse(null);
        return user != null;
    }

    private boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private AdminSignupResponse mapToAdminSignupResponse(User user) {
        logger.debug("Mapping admin user to response: {}", user.getId());
        return AdminSignupResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(user.getRoles().stream().map(Role::toString).collect(Collectors.toSet()))
                .build();
    }

    private UserSignupResponse mapToUserSignupResponse(User user) {
        logger.debug("Mapping regular user to response: {}", user.getId());
        return UserSignupResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream().map(Role::toString).collect(Collectors.toSet()))
                .address(user.getAddress())
                .city(user.getCity())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    private RestaurantOwnerSignupResponse mapToRestaurantOwnerSignupResponse(User user) {
        logger.debug("Mapping restaurant owner to response: {}", user.getId());
        return RestaurantOwnerSignupResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream().map(Role::toString).collect(Collectors.toSet()))
                .phoneNumber(String.valueOf(user.getPhoneNumber()))
                .build();
    }

    private VehicleResponse mapToVehicleResponse(DeliveryVehicle vehicle) {
        logger.debug("Mapping delivery vehicle to response: {}", vehicle.getId());
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .userId(vehicle.getDeliveryPerson().getId())
                .vehicleNumber(vehicle.getVehicleNumber())
                .vehicleType(vehicle.getVehicleType().toString())
                .licenseNumber(vehicle.getDrivingLicense())
                .vehicleImg(vehicle.getVehicleImg())
                .vehicleDocuments(vehicle.getVehicleDocuments())
                .build();
    }

    private DeliveryPersonSignupResponse mapToDeliveryPersonSignupResponse(User user, DeliveryVehicle vehicle) {
        logger.debug("Mapping delivery person to response: {}", user.getId());
        return DeliveryPersonSignupResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles().stream().map(Role::toString).collect(Collectors.toSet()))
                .phoneNumber(String.valueOf(user.getPhoneNumber()))
                .address(user.getAddress())
                .city(user.getCity())
                .nic(user.getNic())
                .postalCode(user.getPostalCode())
                .profileImg(user.getProfileImage())
                .vehicle(mapToVehicleResponse(vehicle))
                .build();
    }
}