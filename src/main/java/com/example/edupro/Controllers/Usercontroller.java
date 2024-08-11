package com.example.edupro.Controllers;

import com.example.edupro.Entity.Role;
import com.example.edupro.Entity.User;
import com.example.edupro.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins ="http://localhost:8080",allowCredentials = "true")
@RequestMapping("/api/user")
public class Usercontroller {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        Optional<User> utilisateur = userService.getUserById(id);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/adduser")
    public User createUtilisateur(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUtilisateur(@PathVariable Integer id, @RequestBody User user) {
        if (userService.getUserById(id).isPresent()) {
            user.setId(id);
            return ResponseEntity.ok(userService.createUser(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        if (userService.getUserById(id).isPresent()) {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/role/tuteur")
    public List<User> getUsersByRoleTuteur() {
        return userService.getUsersByRole(Role.TUTEUR);
    }

    @PostMapping("/{userId}/upload-picture")
    public ResponseEntity<String> uploadUserPicture(
            @PathVariable Integer userId,
            @RequestParam("file") MultipartFile file) {
        try {
            // Use the service to handle the file upload
            userService.updateUserPictureFromFile(userId, file);
            return ResponseEntity.ok("Picture uploaded successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload picture: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found.");
        }
    }
    @DeleteMapping("/{userId}/delete-picture")
    public ResponseEntity<String> deleteUserPicture(@PathVariable Integer userId) {
        try {
            // Use the service to handle the deletion
            userService.deleteUserPicture(userId);
            return ResponseEntity.ok("Picture deleted successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found.");
        }
    }

    @PutMapping("/{userId}/update-address")
    public ResponseEntity<User> updateUserAddress(
            @PathVariable Integer userId,
            @RequestBody String adress) {
        Optional<User> userOptional = userService.getUserById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAdress(adress);
            userService.createUser(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
