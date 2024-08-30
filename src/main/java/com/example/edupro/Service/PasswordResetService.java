package com.example.edupro.Service;

import com.example.edupro.Email.EmailService;
import com.example.edupro.Email.EmailTemplateName; // Make sure this is imported

import com.example.edupro.Entity.User;
import com.example.edupro.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;  // Use PasswordEncoder here


    public void processForgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();
            user.setResetToken(token);
            userRepository.save(user);

            String resetUrl = "http://localhost:4200/reset-password?token=" + token;
            try {
                emailService.sendEmail(
                        user.getEmail(),
                        user.getFirstName(),
                        EmailTemplateName.RESET_PASSWORD, // Ensure this is used
                        resetUrl,
                        token,
                        "Password Reset Request"
                );
            } catch (MessagingException e) {
                log.error("Failed to send password reset email to " + user.getEmail(), e);
                throw new RuntimeException("Failed to send password reset email", e);
            }
        }
    }



    public boolean validatePasswordResetToken(String token) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        return userOptional.isPresent();
    }
    public void updatePassword(String token, String newPassword) {
        Optional<User> userOptional = userRepository.findByResetToken(token);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encodedPassword = passwordEncoder.encode(newPassword);  // Hash the password
            user.setPassword(encodedPassword);
            user.setResetToken(null);
            userRepository.save(user);
        }
    }

}
