package com.example.edupro.Controllers;

import com.example.edupro.Entity.Subject;
import com.example.edupro.Entity.User;
import com.example.edupro.Repositories.SubjectRepository;
import com.example.edupro.Service.SearchService;
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
    public ResponseEntity<Subject> addSubject(@RequestBody Subject subject) {
        Subject savedSubject = subjectRepository.save(subject);
        return ResponseEntity.ok(savedSubject);
    }


}
