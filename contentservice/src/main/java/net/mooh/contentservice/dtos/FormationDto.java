package net.mooh.contentservice.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationDto {

    private Long id;


    private String titre;

    private String description;

    private String niveau;

    private Integer duree;

    private String langue;

    private boolean publique;

    private boolean active;

    private List<SectionFormationDto> sections = new ArrayList<>();

    private Set<String> prerequis = new HashSet<>();

    private Set<String> objectifs = new HashSet<>();

    private Set<String> tags = new HashSet<>();

    private Long createurId;

    private String createurNom;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    // Statistiques supplémentaires
    private Integer nbSections;
    private Integer nbInscrits;
    private Double tauxCompletion;
}
