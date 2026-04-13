package com.contacthub.contact;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Contact{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String middleName;
    private String lastName;
    private String nickname;
    private String homeAddress;



    private String phone;
    private String email;

    private String address;
    private String birthday;

    private boolean favorite;
    private boolean deleted = false;
    private java.time.LocalDateTime deletedAt;

    private String accountType;

    private String notes;


}
