package com.contacthub.contact;

import com.contacthub.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByIdAndUser(Long id, User user);

    List<Contact> findByUserAndDeletedFalse(User user);

    List<Contact> findByUserAndDeletedTrue(User user);

    List<Contact> findByFirstNameContainingIgnoreCaseAndDeletedFalseAndUser(String name, User user);

    List<Contact> findByFirstNameStartingWithIgnoreCaseAndDeletedFalseAndUser(String letter, User user);

    List<Contact> findByFavoriteTrueAndDeletedFalseAndUser(User user);
    List<Contact> findByGroupIdAndUser(Long groupId, User user);
    Optional<Contact> findByShareId(String shareId);

}