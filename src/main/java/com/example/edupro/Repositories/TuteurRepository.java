package com.example.edupro.Repositories;

import com.example.edupro.Entity.Tuteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuteurRepository extends JpaRepository<Tuteur, Integer> {


}
