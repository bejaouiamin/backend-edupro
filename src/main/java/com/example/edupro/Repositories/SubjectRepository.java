package com.example.edupro.Repositories;

import com.example.edupro.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    List<Subject> findByNameContainingIgnoreCase(String name);
    Optional<Subject> findByName(String name);


}
