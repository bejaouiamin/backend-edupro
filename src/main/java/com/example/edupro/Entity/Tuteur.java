package com.example.edupro.Entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Tuteur {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;

    @MapsId
    @OneToOne
    @JoinColumn(name = "id")
    private User user;

    private String title;
    private String educationlevel;
    private String experience;
    private Integer period;
    @ElementCollection
    @CollectionTable(name = "tuteur_skills", joinColumns = @JoinColumn(name = "tuteur_id"))
    private List<Skill> skills;

}
