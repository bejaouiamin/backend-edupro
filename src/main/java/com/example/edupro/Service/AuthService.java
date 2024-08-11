package com.example.edupro.Service;

import com.example.edupro.Controllers.AuthenticationRequest;
import com.example.edupro.Controllers.AuthenticationResponse;
import com.example.edupro.Controllers.RegistrationRequest;
import com.example.edupro.Email.EmailService;
import com.example.edupro.Email.EmailTemplateName;
import com.example.edupro.Entity.User;
import com.example.edupro.Entity.Role;
import com.example.edupro.Repositories.UserRepository;
import com.example.edupro.Security.JwtService;
import com.example.edupro.Token.Token;
import com.example.edupro.Token.TokenRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final TokenRepository tokenRepository;

    public void register(RegistrationRequest request) throws MessagingException {
        Role userRole = determineUserRole(request);

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .dateOfBirth(request.getDateOfBirth())
                .accountLocked(false)
                .enabled(false)
                .role(userRole)
                .picture(request.getPicture())
                .bio(request.getBio())
                .adress(request.getAdress())
                .phone(request.getPhone())
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private Role determineUserRole(RegistrationRequest request) {
        // Example logic to determine role based on the request
        // Adjust this as per your application's requirements
        if (request.getRole() == Role.TUTEUR) {
            return Role.TUTEUR;
        } else if (request.getRole() == Role.ETUDIANT) {
            return Role.ETUDIANT;
        }
        throw new IllegalArgumentException("Invalid role: " + request.getRole());
    }

    //@Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new RuntimeException("Activation token has expired. A new token has been sent to the same email address");
        }

        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);

        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String newToken = generateAndSaveActivationToken(user);
        String activationUrl = "http://localhost:8080/api/auth/activate-account?token=" + newToken; // Adjust base URL as needed

        emailService.sendEmail(
                user.getEmail(),
                user.getFirstName(),
                EmailTemplateName.ACTIVATE_ACCOUNT, // Assuming you have this enum value, or replace it with the correct template name
                activationUrl,
                newToken, // Assuming activation code is the same as the token
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user) {
        // Generate a token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("Username", user.getUsername());

        var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
        System.out.println("Generated JWT Token: " + jwtToken); // Log the generated token
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // Method to find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

}
