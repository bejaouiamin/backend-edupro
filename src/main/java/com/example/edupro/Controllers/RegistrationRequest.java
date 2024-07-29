package com.example.edupro.Controllers;

import com.example.edupro.Entity.Role;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class RegistrationRequest {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private String password;
    private Role role;
    private String picture;
    private String bio;
    private String adress;
    private String phone;
}
