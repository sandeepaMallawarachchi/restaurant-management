package com.user.user_service.controllers;

import com.user.user_service.dto.request.LoginRequest;
import com.user.user_service.dto.request.SignupRequest;
import com.user.user_service.dto.response.JwtResponse;
import com.user.user_service.exception.ResourceNotFoundException;
import com.user.user_service.models.Role;
import com.user.user_service.models.User;
import com.user.user_service.repositories.RoleRepository;
import com.user.user_service.repositories.UserRepository;
import com.user.user_service.services.CustomUserDetails;
import com.user.user_service.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
// Allow only frontend calls
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final JwtUtil jwtUtil;

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Login attempt: " + loginRequest.getUsername());

        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtUtil.generateToken(loginRequest.getUsername());

        // Get user details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        System.out.println("User authenticated: " + userDetails.getUsername());

        // Extract roles
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        // Return JWT response
        return ResponseEntity.ok(new JwtResponse(jwt,
                "Bearer",
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        System.out.println(encoder.encode(signUpRequest.getUsername()));
        // Check if username is already taken
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Username is already taken!");
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Email is already in use!");
        }

        // Create new user account
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));


        // Set roles
        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            // Assign default role if no roles are specified
            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException(" Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                                .orElseThrow(() -> new RuntimeException(" Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName("ROLE_MODERATOR")
                                .orElseThrow(() -> new RuntimeException("Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName("ROLE_USER")
                                .orElseThrow(() -> new RuntimeException(" Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }




    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from the Authorization header
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401)
                        .body(new JwtResponse("Unauthorized", "Bearer", null, null, null, null));
            }

            String token = authHeader.split(" ")[1];  // Get the token part
            // Validate the token
            String username = jwtUtil.extractUsername(token);

            if (username == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401)
                        .body(new JwtResponse("Invalid token", "Bearer", null, null, null, null));
            }

            // Find the user from the database based on the username
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));


            // Return the user details along with the token validation status
            return ResponseEntity.ok(new JwtResponse("Token is valid", "Bearer", user.getId(),
                    user.getUsername(), user.getEmail(), user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList())));

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(new JwtResponse("Server error", "Bearer", null, null, null, null));
        }
    }
}
