package com.contacthub.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String name;
    private String otp;

    // Map this to the 'phone' column seen in your SQL output
    @Column(name = "phone")
    private String phone;

    private boolean enabled = true;

    @Column(name = "profile_pic")
    private String profilePic;
}
