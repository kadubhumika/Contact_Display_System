package com.contacthub.contact;

import com.contacthub.auth.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService service;

    private User getUser(HttpServletRequest request){
        return (User) request.getAttribute("user");
    }


    @GetMapping("/list-all")
    public List<com.contacthub.contact.UIContact> getAll(HttpServletRequest request) {
        return service.getAll(getUser(request));
    }

    @PostMapping("/save")
    public String save(@RequestBody com.contacthub.contact.UIContact contact, HttpServletRequest request) {
        return service.saveContact(contact, getUser(request));
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, HttpServletRequest request) {
        return service.delete(id, getUser(request));
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody com.contacthub.contact.UIContact contact,
                         HttpServletRequest request) {
        return service.update(id, contact, getUser(request));
    }
    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws Exception {
        Path path = Paths.get("uploads/" + filename);
        Resource resource = new UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                .body(resource);
    }

    @PutMapping("/favorite/{id}")
    public String fav(@PathVariable Long id, HttpServletRequest request) {
        return service.toggleFavorite(id, getUser(request));
    }

    @GetMapping("/search")
    public List<com.contacthub.contact.UIContact> search(@RequestParam String name, HttpServletRequest request) {
        return service.search(name, getUser(request));
    }
    @PostMapping("/upload/{id}")
    public String uploadContactImage(@PathVariable Long id,
                                     @RequestParam("file") MultipartFile file,
                                     HttpServletRequest req) {
        return service.uploadContactImage(id, file, getUser(req));
    }

    @GetMapping("/starts-with")
    public List<com.contacthub.contact.UIContact> filter(@RequestParam String letter, HttpServletRequest request) {
        return service.filter(letter, getUser(request));
    }
    @PostMapping("/share/{id}")
    public String share(@PathVariable Long id, HttpServletRequest req) {
        return service.generateShare(id, getUser(req));
    }

    @PostMapping("/import/{shareId}")
    public String importC(@PathVariable String shareId, HttpServletRequest req) {
        return service.importContact(shareId, getUser(req));
    }

    @GetMapping("/favorites")
    public List<com.contacthub.contact.UIContact> getFavorites(HttpServletRequest request) {
        return service.getFavorites(getUser(request));
    }

    @DeleteMapping("/delete-multiple")
    public String deleteMultiple(@RequestBody List<Long> ids, HttpServletRequest request) {
        return service.deleteMultiple(ids, getUser(request));
    }

    @GetMapping("/recent-deleted")
    public List<com.contacthub.contact.UIContact> recentDeleted(HttpServletRequest request) {
        return service.getDeleted(getUser(request));
    }

    @PutMapping("/restore/{id}")
    public String restore(@PathVariable Long id, HttpServletRequest request){
        return service.restore(id, getUser(request));
    }

}