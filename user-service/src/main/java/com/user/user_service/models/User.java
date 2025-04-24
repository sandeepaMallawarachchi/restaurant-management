package com.user.user_service.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"roles"})
@ToString(exclude = {"roles"})
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private LocalDateTime lastLogin;

    @NotBlank(message = "Address is required")
    private String address;

    @ElementCollection
    @CollectionTable(
            name = "user_orders",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "order_id")
    private List<String> orderIds = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "user_restaurants",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "restaurant_id")
    private Set<Long> restaurantIds = new HashSet<>();


    private String city;

    private int postalCode;

    private String profileImage;

    private String nic;

    private boolean availability;

    private boolean verified;

    @ElementCollection
    @CollectionTable(
            name = "delivery_person_restaurants",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "delivery_person_r_id")
    private Set<Long> registeredRestaurants = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "vehicle_id", referencedColumnName = "id")
    private DeliveryVehicle deliveryVehicle;


    @OneToOne
    @JoinColumn(name = "bank_detail_id", referencedColumnName = "id")
    private BankDetail bankDetail;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
