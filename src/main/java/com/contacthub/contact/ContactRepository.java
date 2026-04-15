package com.contacthub.contact;

import com.contacthub.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Contact findByIdAndUser(Long id, User user);

    List<Contact> findByUserAndDeletedFalse(User user);

    List<Contact> findByUserAndDeletedTrue(User user);

    List<Contact> findByFirstNameContainingIgnoreCaseAndDeletedFalseAndUser(String name, User user);

    List<Contact> findByFirstNameStartingWithIgnoreCaseAndDeletedFalseAndUser(String letter, User user);

    List<Contact> findByFavoriteTrueAndDeletedFalseAndUser(User user);
}