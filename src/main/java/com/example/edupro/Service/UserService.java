package com.example.edupro.Service;

import com.example.edupro.Entity.Role;
import com.example.edupro.Entity.User;
import com.example.edupro.Repositories.UserRepository;
import com.example.edupro.Token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;


    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Integer id) {
        tokenRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    // Add this method to save the user after clearing associations
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }



    // Method to handle file upload
    public User updateUserPictureFromFile(Integer userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Convert the file to a byte array
        byte[] pictureData = file.getBytes();

        user.setPicture(pictureData);
        return userRepository.save(user);
    }
    public User deleteUserPicture(Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPicture(null); // Clear the picture
        return userRepository.save(user);
    }

}
