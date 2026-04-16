package com.contacthub.contact;

import com.contacthub.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService {

    @Autowired
    private ContactGroupRepo groupRepo;

    @Autowired
    private ContactRepository contactRepo;

    public String createGroup(String name, User user) {
        ContactGroup g = new ContactGroup();
        g.setName(name);
        g.setUser(user);
        groupRepo.save(g);
        return "Group created";
    }

    public List<ContactGroup> getGroups(User user) {
        return groupRepo.findByUser(user);
    }

    public List<Contact> getContacts(Long groupId, User user) {
        return contactRepo.findByGroupIdAndUser(groupId, user);
    }

    public String assign(Long contactId, Long groupId, User user) {
        Contact c = contactRepo.findByIdAndUser(contactId, user)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        ContactGroup g = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        c.setGroup(g);
        contactRepo.save(c);

        return "Assigned to group";
    }
}