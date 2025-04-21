package com.user.user_service.models;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode(exclude = {"roles", "addresses", "orders"})
@ToString(exclude = {"roles", "addresses", "orders"})
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    private String status; // New field: User's status (active, banned, etc.)
    private LocalDateTime lastLogin; // New field: Last login timestamp


//    private Set<String> addresses = new HashSet<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private Set<Order> orders = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;




    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();


    }


    // Eager fetching to load roles with the user
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
}
