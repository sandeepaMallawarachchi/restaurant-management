package com.user.user_service.controllers;

import com.user.user_service.dto.request.*;
import com.user.user_service.dto.response.*;
import com.user.user_service.exception.ResourceNotFoundException;
import com.user.user_service.models.Role;
import com.user.user_service.models.User;
import com.user.user_service.repositories.UserRepository;
import com.user.user_service.services.CustomUserDetails;
import com.user.user_service.services.SignupService;
import com.user.user_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final SignupService signupService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateToken(loginRequest.getUsername());
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        logger.info("User authenticated successfully: {}", userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                "Bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<AdminSignupResponse> registerAdmin(@Valid @RequestBody AdminSignupRequest request) {
        logger.info("Signup request received for username: {}", request.getUsername());
        return ResponseEntity.ok(signupService.signupAdmin(request));
    }

    @PostMapping("/signup/restaurantowner")
    public ResponseEntity<RestaurantOwnerSignupResponse> registerRestaurantOwner(@Valid @RequestBody RestaurantOwnerSignupRequest request) {
        logger.info("Signup request received for username: {}", request.getUsername());
        return ResponseEntity.ok(signupService.signupRestaurantOwner(request));
    }

    @PostMapping("/signup/deliveryperson")
    public ResponseEntity<DeliveryPersonSignupResponse> registerDeliveryPerson(@Valid @RequestBody DeliveryPersonSignupRequest request) {
        logger.info("Signup request received for username: {}", request.getUsername());
        return ResponseEntity.ok(signupService.signupDeliveryPerson(request));
    }

    @PostMapping("/signup/user")
    public ResponseEntity<UserSignupResponse> registerUser(@Valid @RequestBody UserSignupRequest request) {
        logger.info("Signup request received for username: {}", request.getUsername());
        return ResponseEntity.ok(signupService.signupUser(request));
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
//        logger.info("Signup request received for username: {}", signUpRequest.getUsername());
//
//        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
//            logger.warn("Username already taken: {}", signUpRequest.getUsername());
//            return ResponseEntity.badRequest().body("Username is already taken!");
//        }
//
//        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//            logger.warn("Email already in use: {}", signUpRequest.getEmail());
//            return ResponseEntity.badRequest().body("Email is already in use!");
//        }
//
//        User user = new User();
//        user.setUsername(signUpRequest.getUsername());
//        user.setEmail(signUpRequest.getEmail());
//        user.setPassword(encoder.encode(signUpRequest.getPassword()));
//        user.setFirstName(signUpRequest.getFirstName());
//        user.setLastName(signUpRequest.getLastName());
//        user.setAddress(signUpRequest.getAddress());
//        user.setPhoneNumber(signUpRequest.getPhone());
//
//        Set<String> strRoles = signUpRequest.getRoles();
//        Set<Role> roles = new HashSet<>();
//
//        logger.debug("Assigning roles: {}", strRoles);
//
//        if (strRoles == null || strRoles.isEmpty()) {
//            Role userRole = roleRepository.findByName("ROLE_USER")
//                    .orElseThrow(() -> new RuntimeException("Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                switch (role.toLowerCase()) {
//                    case "admin":
//                        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
//                                .orElseThrow(() -> new RuntimeException("Role is not found."));
//                        roles.add(adminRole);
//                        break;
//                    case "restaurantowner":
//                        Role ownerRole = roleRepository.findByName("ROLE_RESTAURANT_OWNER")
//                                .orElseThrow(() -> new RuntimeException("Role is not found."));
//                        roles.add(ownerRole);
//                        break;
//                    default:
//                        Role defaultRole = roleRepository.findByName("ROLE_USER")
//                                .orElseThrow(() -> new RuntimeException("Role is not found."));
//                        roles.add(defaultRole);
//                }
//            });
//        }
//
//        user.setRoles(roles);
//        userRepository.save(user);
//
//        logger.info("User registered successfully: {}", user.getUsername());
//        return ResponseEntity.ok("User registered successfully!");
//    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        logger.info("Token validation request received");

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("Missing or invalid Authorization header");
                return ResponseEntity.status(401)
                        .body(new JwtResponse("Unauthorized", "Bearer", null, null, null, null));
            }

            String token = authHeader.split(" ")[1];
            logger.debug("Extracted token: {}", token);

            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token)) {
                logger.warn("Invalid token for user: {}", username);
                return ResponseEntity.status(401)
                        .body(new JwtResponse("Invalid token", "Bearer", null, null, null, null));
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            logger.info("Token is valid for user: {}", username);

            return ResponseEntity.ok(new JwtResponse("Token is valid", "Bearer", user.getId(),
                    user.getUsername(), user.getEmail(), user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList())));

        } catch (Exception e) {
            logger.error("Error while validating token", e);
            return ResponseEntity.status(500)
                    .body(new JwtResponse("Server error", "Bearer", null, null, null, null));
        }
    }
}
