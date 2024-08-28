package com.example.edupro.Controllers;

import com.example.edupro.Entity.User;
import com.example.edupro.Security.JwtService;
import com.example.edupro.Service.AuthService;
import com.example.edupro.Service.PasswordResetService;
import jakarta.mail.MessagingException;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;


@RestController
@CrossOrigin(origins ="http://localhost:8080",allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthService authService;
    private final JwtService jwtservice;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws MessagingException {
        authService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<AuthenticationResponse> getLoginInfo(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthenticationResponse("User not authenticated"));
        }

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        User user = authService.findByEmail(email);
        if (user == null) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(name.split(" ")[0]);
            user.setLastName(name.split(" ")[1]);
            user.setEnabled(true);
            authService.save(user);
        }

        // Generate a token with an empty map as extraClaims and the UserDetails object
        String token = jwtservice.generateToken(new HashMap<>(), user); // Use an empty map for extraClaims

        return ResponseEntity.ok(new AuthenticationResponse(token)); // Only pass the token
    }


    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authService.activateAccount(token);
    }

    @GetMapping("/me")
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return authService.findByEmail(currentUserName); // Assuming you identify users by email
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        passwordResetService.processForgotPassword(request.get("email"));
        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }
    @GetMapping("/reset-password")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        if (passwordResetService.validatePasswordResetToken(token)) {
            return ResponseEntity.ok("Token is valid.");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired token.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");
        passwordResetService.updatePassword(token, newPassword);
        return ResponseEntity.ok("Password has been reset.");
    }



}
