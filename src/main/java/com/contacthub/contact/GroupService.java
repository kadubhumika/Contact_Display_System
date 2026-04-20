package com.contacthub.contact;

import com.contacthub.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class GroupService {
    private UIContact map(Contact c) {
        UIContact dto = new UIContact();
        dto.setId(c.getId());
        dto.setName(c.getFirstName());
        dto.setPhone(c.getPhone());
        dto.setEmail(c.getEmail());
        dto.setFavorite(c.isFavorite());
        return dto;
    }

    @Autowired
    private ContactGroupRepo groupRepo;

    @Autowired
    private ContactRepository contactRepo;

    public Long createGroup(String name, User user) {
        ContactGroup g = new ContactGroup();
        g.setName(name);
        g.setUser(user);
        groupRepo.save(g);

        return g.getId();
    }

    public List<ContactGroup> getGroups(User user) {
        return groupRepo.findByUser(user);
    }

    public List<UIContact> getContacts(Long groupId, User user) {
        return contactRepo.findByGroupIdAndUser(groupId, user)
                .stream()
                .map(this::map)
                .toList();
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
    public String createGroupWithMembers(String name, List<Integer> members, User user) {

        ContactGroup g = new ContactGroup();
        g.setName(name);
        g.setUser(user);
        groupRepo.save(g);

        for(Integer id : members){
            Contact c = contactRepo.findByIdAndUser(Long.valueOf(id), user).orElseThrow();
            c.setGroup(g);
            contactRepo.save(c);
        }

        return "Group Created";
    }
    public String assignMultiple(List<Long> contactIds, Long groupId, User user) {

        ContactGroup g = groupRepo.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        for(Long id : contactIds){
            Contact c = contactRepo.findByIdAndUser(id, user)
                    .orElse(null);

            if(c != null){
                c.setGroup(g);
                contactRepo.save(c);
            }
        }

        return "Contacts added to group";
    }
    public String createGroupWithImage(String name, MultipartFile file, User user) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            ContactGroup g = new ContactGroup();
            g.setName(name);
            g.setGroupPic(fileName);
            g.setUser(user);

            groupRepo.save(g);

            return "Group Created";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}