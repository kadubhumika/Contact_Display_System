package com.contacthub.contact;

import com.contacthub.auth.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String address;
    private String phone;
    private String email;
    private String birthday;

    private boolean favorite = false;
    private boolean deleted = false;

    private java.time.LocalDateTime deletedAt;

    private String accountType;

    @Column(length = 1000)
    private String notes;

    // This runs automatically before saving to DB, replacing your manual constructor
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    private String profilePic;


    @ManyToOne
    private ContactGroup group;
    private String shareId;

    @ManyToOne
    @JoinColumn(name = "user_id") // Use the actual ID column name from your DB
    private User user;




}

