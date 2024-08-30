package com.example.edupro.Controllers;

import com.example.edupro.Entity.Subject;
import com.example.edupro.Entity.User;
import com.example.edupro.Repositories.SubjectRepository;
import com.example.edupro.Service.SearchService;
import com.example.edupro.Service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins ="http://localhost:8080",allowCredentials = "true")
@RequiredArgsConstructor
@RequestMapping("/api/sear")
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private UserService userService;
    private final SubjectRepository subjectRepository;

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchTutors(@RequestParam String subject, @RequestParam String adress) {
        List<User> tutors = searchService.searchTutors(subject, adress);
        if (tutors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or return a custom message
        }
        return ResponseEntity.ok(tutors);
    }


    @GetMapping("/subjects")
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectRepository.findAll();
        return ResponseEntity.ok(subjects);
    }

    @PostMapping("/addsubjects")
    public ResponseEntity<String> addSubject(@RequestBody Subject subject, @RequestParam Integer userId) {
        System.out.println("Received request to add subject with userId: " + userId);

        // Fetch the user by ID
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if the user already has a subject
        if (!user.getSubjects().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already has a subject.");
        }

        // Check if the subject already exists in the database
        Optional<Subject> existingSubjectOptional = searchService.findByName(subject.getName());

        // If the subject exists, link it to the user, otherwise, create a new one
        if (existingSubjectOptional.isPresent()) {
            user.getSubjects().add(existingSubjectOptional.get());
        } else {
            // Save the new subject
            Subject savedSubject = searchService.save(subject);
            user.getSubjects().add(savedSubject);
        }

        // Save the user with the linked subject
        userService.saveUser(user);

        return ResponseEntity.ok("Subject added successfully.");
    }

    @Transactional
    @DeleteMapping("/deletesubject")
    public ResponseEntity<String> deleteSubject(@RequestParam Integer subjectId, @RequestParam Integer userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        // Check if the user has the subject
        if (!user.getSubjects().contains(subject)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subject not found for this user.");
        }

        // Remove the subject from the user's list of subjects
        user.getSubjects().remove(subject);
        userService.saveUser(user); // Save user to update the relationship

        // Remove the user from the subject's list of users
        subject.getUsers().remove(user);
        subjectRepository.saveAndFlush(subject); // Save subject to update the relationship

        // If no users are associated with the subject anymore, delete the subject
        if (subject.getUsers().isEmpty()) {
            subjectRepository.delete(subject);
        }

        return ResponseEntity.ok("Subject successfully deleted.");
    }




}
