package com.contacthub.contact;
import com.contacthub.contact.UIContact;

import com.contacthub.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


@Service
public class ContactService {
    private UIContact map(Contact c) {
        UIContact dto = new UIContact();
        dto.setId(c.getId());
        dto.setName(c.getFirstName());
        dto.setPhone(c.getPhone());
        dto.setEmail(c.getEmail());
        dto.setFavorite(c.isFavorite());
        dto.setProfilePic(c.getProfilePic());
        return dto;
    }

    @Autowired
    private ContactRepository repo;

    public String saveContact(UIContact dto, User user) {
        if (user == null) throw new RuntimeException("Unauthorized");

        Contact c = new Contact();

        c.setFirstName(dto.getName());   // simple mapping
        c.setPhone(dto.getPhone());
        c.setEmail(dto.getEmail());
        c.setUser(user);

        repo.save(c);

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

    public List<UIContact> getAll(User user) {
        return repo.findByUserAndDeletedFalse(user)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<UIContact> getDeleted(User user) {
        return repo.findByUserAndDeletedTrue(user)
                .stream()
                .map(this::map)
                .toList();
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

    public String update(Long id, UIContact newData, User user) {
        Contact c = repo.findByIdAndUser(id, user)
                .orElse(null);
        if (c == null) return "Not Found";

        c.setFirstName(newData.getName());
        c.setPhone(newData.getPhone());
        c.setEmail(newData.getEmail());

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
    public String uploadImage(Long id, MultipartFile file, User user) {
        try {
            Contact c = repo.findByIdAndUser(id, user).orElseThrow();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            c.setProfilePic(fileName);
            repo.save(c);

            return "Uploaded";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<UIContact> search(String name, User user) {
        return repo.findByFirstNameContainingIgnoreCaseAndDeletedFalseAndUser(name, user)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<UIContact> filter(String letter, User user) {
        return repo.findByFirstNameStartingWithIgnoreCaseAndDeletedFalseAndUser(letter, user)
                .stream()
                .map(this::map)
                .toList();
    }

    public List<UIContact> getFavorites(User user){
        return repo.findByFavoriteTrueAndDeletedFalseAndUser(user)
                .stream()
                .map(this::map)
                .toList();
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
    public String saveWithImage(String name, String phone, String email,
                                MultipartFile file, User user) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            Contact c = new Contact();
            c.setFirstName(name);
            c.setPhone(phone);
            c.setEmail(email);
            c.setProfilePic(fileName);
            c.setUser(user);

            repo.save(c);

            return "Saved";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String uploadContactImage(Long id, MultipartFile file, User user) {
        try {
            Contact c = repo.findByIdAndUser(id, user)
                    .orElseThrow(() -> new RuntimeException("Not found"));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            Path path = Paths.get("uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            c.setProfilePic(fileName);
            repo.save(c);

            return "Uploaded";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}