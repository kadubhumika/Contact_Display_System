package com.contacthub.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    @Autowired
    private AuthRepo repo;

    @Autowired
    private JWTUtility jwtUtility;

    public String signup(User user){
        repo.save(user);

        return user.getEmail() + " Registered Successfully";
    }

    public String login(User user) {

        User existing = repo.findByEmail(user.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!existing.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtility.generateToken(user.getEmail());
    }
}