package com.contacthub.auth;

import com.contacthub.contact.Contact;
import com.contacthub.contact.ContactRepository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


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

    public String deletePhoto(User user) {
        user.setProfilePic(null);
        repo.save(user);
        return "Deleted";
    }
    public ResponseEntity<Resource> getPhoto(String filename) {
        try {
            Path path = Paths.get("uploads").resolve(filename);
            Resource res = new UrlResource(path.toUri());

            if (!res.exists() || !res.isReadable()) {
                throw new RuntimeException("File not found");
            }

            return ResponseEntity.ok().body(res);

        } catch (Exception e) {
            throw new RuntimeException("Not found");
        }
    }

    public String login(User user) {
        // Trim the email in case of trailing spaces from the UI
        User existing = repo.findByEmail(user.getEmail().trim())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Trim the password comparison
        if (!existing.getPassword().trim().equals(user.getPassword().trim())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtility.generateToken(existing.getEmail());
    }

}