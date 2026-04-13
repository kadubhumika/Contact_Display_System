package com.contacthub.contact;

import com.contacthub.contact.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import org.springframework.data.domain.Sort;
@Service
public class ContactService {

    @Autowired
    private ContactRepository repo;

    // SAVE
    public String saveContact(Contact contact) {
        repo.save(contact);
        return "Contact Saved Successfully";
    }

    // GET ALL
    public List<Contact> getAll() {
        return repo.findAll();
    }
    public List<Contact> getAll() {
        return repo.findByDeletedFalse();
    }
    public List<Contact> getDeleted() {
        return repo.findByDeletedTrue();
    }

    // DELETE
    public String delete(Long id) {
        Contact c = repo.findById(id).orElse(null);

        if (c == null) return "Not Found";

        c.setDeleted(true);
        c.setDeletedAt(java.time.LocalDateTime.now());

        repo.save(c);

        return "Moved to Recently Deleted";
    }

    // UPDATE
    public String update(Long id, Contact newData) {
        Contact c = repo.findById(id).orElse(null);

        if (c == null) return "Contact Not Found";

        c.setFirstName(newData.getFirstName());
        c.setMiddleName(newData.getMiddleName());
        c.setLastName(newData.getLastName());
        c.setPhone(newData.getPhone());
        c.setEmail(newData.getEmail());
        c.setAddress(newData.getAddress());
        c.setNickname(newData.getNickname());
        c.setNotes(newData.getNotes());


        repo.save(c);

        return "Updated Successfully";
    }

    // FAVORITE
    public String toggleFavorite(Long id) {
        Contact c = repo.findById(id).orElse(null);
        if (c == null) return "Not found";

        c.setFavorite(!c.isFavorite());
        repo.save(c);

        return "Favorite Updated";
    }

    // SEARCH
    public List<Contact> search(String name) {
        return repo.findByFirstNameContainingIgnoreCase(name);
    }

    // FILTER A-Z
    public List<Contact> filter(String letter) {
        return repo.findByFirstNameStartingWithIgnoreCase(letter);
    }
    public String addToFavorite(Long id) {
        Contact c = repo.findById(id).orElse(null);

        if (c == null) return "Contact Not Found";

        c.setFavorite(true);
        repo.save(c);

        return "Added to Favorites";
    }


    public List<Contact> getFavorites(){
        return repo.findByFavoriteTrue();
    }
    public String removeFromFavorite(Long id) {
        Contact c = repo.findById(id).orElse(null);

        if (c == null) return "Contact Not Found";

        c.setFavorite(false);
        repo.save(c);

        return "Removed from Favorites";
    }
    public List<Contact> getSorted(){
        return repo.findAll(Sort.by("firstName").ascending());
    }
    public String deleteMultiple(List<Long> ids) {
        repo.deleteAllById(ids);
        return "Multiple Contacts Deleted";
    }

}