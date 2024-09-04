package com.example.edupro.Service;

import com.example.edupro.Entity.Tuteur;
import com.example.edupro.Repositories.TuteurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TuteurService {
    @Autowired
    private TuteurRepository tuteurRepository;

    public List<Tuteur> getAllTuteurs() {
        return tuteurRepository.findAll();
    }

    public Optional<Tuteur> getTuteurById(Integer id) {
        return tuteurRepository.findById(id);
    }

    public Tuteur createTuteur(Tuteur tuteur) {
        return tuteurRepository.save(tuteur);
    }

    public void deleteTuteur(Integer id) { tuteurRepository.deleteById(id); }
    public Tuteur updateTuteur(Tuteur tuteur) {
        // Ensure the Tuteur exists before updating
        Optional<Tuteur> existingTuteur = tuteurRepository.findById(tuteur.getId());
        if (existingTuteur.isPresent()) {
            return tuteurRepository.save(tuteur); // Update existing record
        } else {
            throw new EntityNotFoundException("Tuteur not found with id: " + tuteur.getId());
        }
    }


}
