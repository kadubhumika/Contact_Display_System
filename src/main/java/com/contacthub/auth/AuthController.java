package com.contacthub.auth;

import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/signup")
    public String signup(@RequestBody User user) {
        return service.signup(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody User user) {
        return service.login(user);
    }

    @DeleteMapping("/delete-photo")
    public String delete(HttpServletRequest req) {
        return service.deletePhoto((User) req.getAttribute("user"));
    }

    @GetMapping("/get-photo")
    public ResponseEntity<Resource> get(@RequestParam String filename) {
        return service.getPhoto(filename);
    }
    @GetMapping("/photo/{filename}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {
        return service.getPhoto(filename);
    }

}