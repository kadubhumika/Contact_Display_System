package com.contacthub.auth;

import com.contacthub.contact.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AuthRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}