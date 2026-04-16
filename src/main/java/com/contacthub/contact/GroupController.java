package com.contacthub.contact;

import com.contacthub.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService service;

    private User getUser(HttpServletRequest req) {
        return (User) req.getAttribute("user");
    }

    @PostMapping("/create-group")
    public String create(@RequestBody Map<String, String> body,
                         HttpServletRequest req) {
        return service.createGroup(body.get("name"), getUser(req));
    }

    @GetMapping("/show-group")
    public List<ContactGroup> all(HttpServletRequest req) {
        return service.getGroups(getUser(req));
    }


    @GetMapping("/{id}/contacts")
    public List<Contact> contacts(@PathVariable Long id,
                                  HttpServletRequest req) {
        return service.getContacts(id, getUser(req));
    }

    @PutMapping("/assign/{contactId}/{groupId}")
    public String assign(@PathVariable Long contactId,
                         @PathVariable Long groupId,
                         HttpServletRequest req) {
        return service.assign(contactId, groupId, getUser(req));
    }
}