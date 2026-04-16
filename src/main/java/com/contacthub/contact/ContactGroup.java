package com.contacthub.contact;

import com.contacthub.auth.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ContactGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    private User user;



}


