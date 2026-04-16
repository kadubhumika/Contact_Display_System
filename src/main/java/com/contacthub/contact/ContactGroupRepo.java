package com.contacthub.contact;

import com.contacthub.auth.User;
import com.contacthub.contact.ContactGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactGroupRepo extends JpaRepository<ContactGroup, Long> {
    List<ContactGroup> findByUser(User user);
}