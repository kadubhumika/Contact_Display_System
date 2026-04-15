package com.contacthub.contact;

import com.contacthub.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService service;

    private User getUser(HttpServletRequest request){
        return (User) request.getAttribute("user");
    }

    @GetMapping
    public List<Contact> getAll(HttpServletRequest request) {
        return service.getAll(getUser(request));
    }

    @PostMapping("/save")
    public String save(@RequestBody Contact contact, HttpServletRequest request) {
        return service.saveContact(contact, getUser(request));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, HttpServletRequest request) {
        return service.delete(id, getUser(request));
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody Contact contact,
                         HttpServletRequest request) {
        return service.update(id, contact, getUser(request));
    }

    @PutMapping("/favorite/{id}")
    public String fav(@PathVariable Long id, HttpServletRequest request) {
        return service.toggleFavorite(id, getUser(request));
    }

    @GetMapping("/search")
    public List<Contact> search(@RequestParam String name, HttpServletRequest request) {
        return service.search(name, getUser(request));
    }

    @GetMapping("/starts-with")
    public List<Contact> filter(@RequestParam String letter, HttpServletRequest request) {
        return service.filter(letter, getUser(request));
    }

    @GetMapping("/favorites")
    public List<Contact> getFavorites(HttpServletRequest request) {
        return service.getFavorites(getUser(request));
    }

    @DeleteMapping("/delete-multiple")
    public String deleteMultiple(@RequestBody List<Long> ids, HttpServletRequest request) {
        return service.deleteMultiple(ids, getUser(request));
    }

    @GetMapping("/recent-deleted")
    public List<Contact> recentDeleted(HttpServletRequest request) {
        return service.getDeleted(getUser(request));
    }

    @PutMapping("/restore/{id}")
    public String restore(@PathVariable Long id, HttpServletRequest request){
        return service.restore(id, getUser(request));
    }
}