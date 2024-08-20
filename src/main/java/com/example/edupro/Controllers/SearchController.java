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

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if the subject name already exists for the user
        boolean subjectExists = user.getSubjects().stream()
                .anyMatch(existingSubject -> existingSubject.getName().equalsIgnoreCase(subject.getName()));

        if (subjectExists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Subject already added.");
        }

        // Save the new subject
        Subject savedSubject = searchService.save(subject); // Ensure subjectService saves and returns the entity

        user.getSubjects().add(savedSubject);
        userService.saveUser(user); // Make sure to save the user after updating their subjects

        return ResponseEntity.ok("Subject added successfully.");
    }


    @Transactional
    @DeleteMapping("/deletesubject")
    public ResponseEntity<String> deleteSubject(@RequestParam Integer subjectId, @RequestParam Integer userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        if (!user.getSubjects().contains(subject)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subject not found for this user.");
        }

        subject.getUsers().remove(user);

        subjectRepository.saveAndFlush(subject);

        user.getSubjects().remove(subject);

        userService.saveUser(user);

        subjectRepository.delete(subject);

        return ResponseEntity.ok("Subject successfully deleted.");
    }



}
