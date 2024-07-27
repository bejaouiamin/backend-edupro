package com.example.edupro.Controllers;

import com.example.edupro.Entity.Tuteur;
import com.example.edupro.Service.TuteurService;
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

    @GetMapping
    public List<Tuteur> getAllTuteurs() {
        return tuteurService.getAllTuteurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tuteur> getTuteurById(@PathVariable Integer id) {
        Optional<Tuteur> tuteur = tuteurService.getTuteurById(id);
        return tuteur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/addTuteur")
    public Tuteur createTuteur(@RequestBody Tuteur tuteur) {
        return tuteurService.createTuteur(tuteur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tuteur> updateTuteur(@PathVariable Integer id, @RequestBody Tuteur tuteur) {
        if (tuteurService.getTuteurById(id).isPresent()) {
            tuteur.setId(id);
            return ResponseEntity.ok(tuteurService.createTuteur(tuteur));
        } else {
            return ResponseEntity.notFound().build();
        }
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
