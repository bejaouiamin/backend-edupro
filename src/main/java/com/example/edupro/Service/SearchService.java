package com.example.edupro.Service;

import com.example.edupro.Entity.Role;
import com.example.edupro.Entity.Subject;
import com.example.edupro.Entity.User;
import com.example.edupro.Repositories.SubjectRepository;
import com.example.edupro.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public List<User> searchTutors(String subjectName, String adress) {
        List<Subject> subjects = subjectRepository.findByNameContainingIgnoreCase(subjectName);
        // Filter users by address, subjects, and role
        List<User> tutors = userRepository.findByAdressContainingIgnoreCaseAndSubjectsIn(adress, subjects)
                .stream()
                .filter(user -> user.getRole() == Role.TUTEUR)
                .collect(Collectors.toList());
        return tutors;
    }
    public Subject save(Subject subject) {
        return subjectRepository.save(subject); // This will now return the saved entity
    }
    public Optional<Subject> findByName(String name) {
        return subjectRepository.findByName(name);
    }

}
