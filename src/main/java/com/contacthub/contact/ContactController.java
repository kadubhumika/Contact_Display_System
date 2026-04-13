package com.contacthub.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService service;

    // ADD
    @PostMapping("/add-contact")
    public String add(@RequestBody Contact contact) {
        return service.saveContact(contact);
    }

    // GET ALL
    @GetMapping
    public List<Contact> getAll() {
        return service.getAll();
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return service.delete(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody Contact contact) {
        return service.update(id, contact);
    }

    // FAVORITE
    @PutMapping("/favorite/{id}")
    public String fav(@PathVariable Long id) {
        return service.toggleFavorite(id);
    }

    // SEARCH
    @GetMapping("/search")
    public List<Contact> search(@RequestParam String name) {
        return service.search(name);
    }

    // FILTER A-Z
    @GetMapping("/starts-with")
    public List<Contact> filter(@RequestParam String letter) {
        return service.filter(letter);
    }
    @GetMapping("/favorites")
    public List<Contact> getFavorites() {
        return service.getFavorites();
    }
    @PutMapping("/favorite/add/{id}")
    public String addFavorite(@PathVariable Long id) {
        return service.addToFavorite(id);
    }
    @PutMapping("/favorite/remove/{id}")
    public String removeFavorite(@PathVariable Long id){
        return service.removeFromFavorite(id);
    }
    @GetMapping("/sorted")
    public List<Contact> sorted() {
        return service.getSorted();
    }
    @DeleteMapping("/delete-multiple")
    public String deleteMultiple(@RequestBody List<Long> ids) {
        return service.deleteMultiple(ids);
    }
    @GetMapping("/recent-deleted")
    public List<Contact> recentDeleted() {
        return service.getDeleted();
    }
}