package com.example.edupro.Repositories;

import com.example.edupro.Entity.Role;
import com.example.edupro.Entity.Subject;
import com.example.edupro.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByAdressContainingIgnoreCaseAndSubjectsIn(String adress, List<Subject> subjects);
    Optional<User> findByResetToken(String resetToken);

}
