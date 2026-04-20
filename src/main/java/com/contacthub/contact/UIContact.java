package com.contacthub.contact;

import lombok.Data;

@Data
public class UIContact {
    private Long id;
    private String name;
    private String phone;
    private String email;

    private boolean favorite; // Required for the Blue Toggle
    private String profilePic;
}