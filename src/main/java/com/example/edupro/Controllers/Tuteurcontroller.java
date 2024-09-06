package com.example.edupro.Controllers;

import com.example.edupro.Entity.Tuteur;
import com.example.edupro.Entity.User;
import com.example.edupro.Service.TuteurService;
import com.example.edupro.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins ="http://localhost:8080",allowCredentials = "true")
@RequestMapping("/api/tuteurs")

public class Tuteurcontroller {

    @Autowired
    private TuteurService tuteurService;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<Tuteur> getAllTuteurs() {
        return tuteurService.getAllTuteurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tuteur> getTuteurById(@PathVariable Integer id) {
        System.out.println("Received ID: " + id); // Add this line
        Optional<Tuteur> tuteur = tuteurService.getTuteurById(id);
        return tuteur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/addTuteur")
    public Tuteur createTuteur(@RequestBody Tuteur tuteur) {
        if (tuteur.getUser() == null || tuteur.getUser().getId() == null) {
            throw new IllegalArgumentException("User must be set before creating Tuteur");
        }

        // Fetch the user from the database to ensure it is attached
        Optional<User> userOptional = userService.getUserById(tuteur.getUser().getId()); // Assuming you have a userService that can fetch users
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found with ID: " + tuteur.getUser().getId());
        }

        // Set the attached User entity to the Tuteur
        tuteur.setUser(userOptional.get());

        // Now persist the Tuteur entity
        return tuteurService.createTuteur(tuteur);
    }



    @PutMapping("/updateTuteur")
    public Tuteur updateTuteur(@RequestBody Tuteur tuteur) {
        if (tuteur.getUser() == null || tuteur.getUser().getId() == null) {
            throw new IllegalArgumentException("User must be set before updating Tuteur");
        }
        return tuteurService.updateTuteur(tuteur);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTuteur(@PathVariable Integer id) {
        if (tuteurService.getTuteurById(id).isPresent()) {
            tuteurService.deleteTuteur(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
