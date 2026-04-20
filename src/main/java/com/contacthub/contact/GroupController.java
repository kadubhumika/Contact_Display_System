package com.contacthub.contact;

import com.contacthub.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
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
    public String create(@RequestBody Map<String, Object> body,
                         HttpServletRequest req) {

        String name = (String) body.get("name");
        List<Integer> members = (List<Integer>) body.get("members");

        return service.createGroupWithMembers(name, members, getUser(req));
    }

    @GetMapping("/show-group")
    public List<ContactGroup> all(HttpServletRequest req) {
        return service.getGroups(getUser(req));
    }


    @GetMapping("/{id}/contacts")
    public List<UIContact> contacts(@PathVariable Long id,
                                    HttpServletRequest req) {
        return service.getContacts(id, getUser(req));
    }

    @PutMapping("/assign/{contactId}/{groupId}")
    public String assign(@PathVariable Long contactId,
                         @PathVariable Long groupId,
                         HttpServletRequest req) {
        return service.assign(contactId, groupId, getUser(req));
    }
    @PostMapping("/assign-multiple/{groupId}")
    public String assignMultiple(@PathVariable Long groupId,
                                 @RequestBody List<Long> contactIds,
                                 HttpServletRequest req) {
        return service.assignMultiple(contactIds, groupId, getUser(req));
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path path = Paths.get("uploads").resolve(filename);
            Resource res = new UrlResource(path.toUri());

            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            throw new RuntimeException("Image not found");
        }
    }
}