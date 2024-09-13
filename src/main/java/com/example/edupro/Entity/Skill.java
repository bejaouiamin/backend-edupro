package com.example.edupro.Entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Skill {
    private String name;
    private Integer percentage;

}
