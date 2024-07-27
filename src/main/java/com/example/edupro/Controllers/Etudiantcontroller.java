package com.example.edupro.Controllers;

import com.example.edupro.Entity.Etudiant;
import com.example.edupro.Service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins ="http://localhost:8080",allowCredentials = "true")
@RequestMapping("/api/etudiants")
public class Etudiantcontroller {
    @Autowired
    private EtudiantService etudiantService;

    @GetMapping
    public List<Etudiant> getAllEtudiants() {
        return etudiantService.getAllEtudiants();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getÉtudiantById(@PathVariable Integer id) {
        Optional<Etudiant> etudiant = etudiantService.getEtudiantById(id);
        return etudiant.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Etudiant createÉtudiant(@RequestBody Etudiant etudiant) {
        return etudiantService.createEtudiant(etudiant);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateÉtudiant(@PathVariable Integer id, @RequestBody Etudiant etudiant) {
        if (etudiantService.getEtudiantById(id).isPresent()) {
            etudiant.setId(id);
            return ResponseEntity.ok(etudiantService.createEtudiant(etudiant));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteÉtudiant(@PathVariable Integer id) {
        if (etudiantService.getEtudiantById(id).isPresent()) {
            etudiantService.deleteEtudiant(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
