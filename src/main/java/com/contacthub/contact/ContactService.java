package com.contacthub.contact;

import com.contacthub.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;

@Service
public class ContactService {

    @Autowired
    private ContactRepository repo;

    public String saveContact(Contact contact, User user) {
        if(user == null) throw new RuntimeException("Unauthorized");

        contact.setUser(user);
        repo.save(contact);

        return "Saved";
    }
    public String generateShare(Long id, User user) {

        Contact c = repo.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Not found"));

        String shareId = UUID.randomUUID().toString().substring(0, 6);

        c.setShareId(shareId);
        repo.save(c);

        return shareId;
    }
    public String importContact(String shareId, User user) {

        Contact original = repo.findByShareId(shareId)
                .orElseThrow(() -> new RuntimeException("Invalid share ID"));

        Contact copy = new Contact();

        copy.setFirstName(original.getFirstName());
        copy.setLastName(original.getLastName());
        copy.setPhone(original.getPhone());
        copy.setEmail(original.getEmail());
        copy.setUser(user);

        repo.save(copy);

        return "Imported successfully";
    }

    public List<Contact> getAll(User user) {
        return repo.findByUserAndDeletedFalse(user);
    }

    public List<Contact> getDeleted(User user) {
        return repo.findByUserAndDeletedTrue(user);
    }

    public String delete(Long id, User user) {
        Contact c = repo.findByIdAndUser(id, user)
                .orElse(null);
        if (c == null) return "Not Found";

        c.setDeleted(true);
        c.setDeletedAt(java.time.LocalDateTime.now());
        repo.save(c);

        return "Moved to Recently Deleted";
    }

    public String update(Long id, Contact newData, User user) {
        Contact c = repo.findByIdAndUser(id, user)
                .orElse(null);
        if (c == null) return "Not Found";

        c.setFirstName(newData.getFirstName());
        c.setMiddleName(newData.getMiddleName());
        c.setLastName(newData.getLastName());
        c.setPhone(newData.getPhone());
        c.setEmail(newData.getEmail());
        c.setAddress(newData.getAddress());
        c.setNickname(newData.getNickname());
        c.setNotes(newData.getNotes());

        repo.save(c);
        return "Updated";
    }

    public String toggleFavorite(Long id, User user) {
        Contact c = repo.findByIdAndUser(id, user)
                .orElse(null);
        if (c == null) return "Not Found";

        c.setFavorite(!c.isFavorite());
        repo.save(c);

        return "Favorite Updated";
    }

    public List<Contact> search(String name, User user) {
        return repo.findByFirstNameContainingIgnoreCaseAndDeletedFalseAndUser(name, user);
    }

    public List<Contact> filter(String letter, User user) {
        return repo.findByFirstNameStartingWithIgnoreCaseAndDeletedFalseAndUser(letter, user);
    }

    public List<Contact> getFavorites(User user){
        return repo.findByFavoriteTrueAndDeletedFalseAndUser(user);
    }

    public String deleteMultiple(List<Long> ids, User user) {
        for(Long id : ids){
            Contact c = repo.findByIdAndUser(id, user)
                    .orElse(null);
            if(c != null){
                c.setDeleted(true);
                c.setDeletedAt(java.time.LocalDateTime.now());
                repo.save(c);
            }
        }
        return "Deleted";
    }

    public String restore(Long id, User user){
        Contact c = repo.findByIdAndUser(id, user)
                .orElse(null);
        if(c == null) return "Not Found";

        c.setDeleted(false);
        c.setDeletedAt(null);
        repo.save(c);

        return "Restored";
    }
}