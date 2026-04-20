package com.contacthub.ui;

public class ContactBox {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String profilePic;

    public ContactBox(Long id,String name, String phone, String email,String profilePic) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.profilePic = profilePic;
    }

    public String getName() { return name; }
    public Long getId() { return id; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public String getProfilePic() {
        return profilePic;
    }

    @Override
    public String toString() {
        return name;
        // shown in list
    }

}