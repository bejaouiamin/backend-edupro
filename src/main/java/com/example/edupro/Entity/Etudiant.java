package com.example.edupro.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Etudiant {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private User user;

    private String educationlevel;


    private String objectif;
}
