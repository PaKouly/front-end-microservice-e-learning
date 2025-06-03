package net.mooh.evaluationservice.dtos;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReponseApprenantDto {

    private Long id;

    private Long evaluationId;

    private Long questionId;

    private String questionEnonce;

    private Set<Long> choixSelectionnes = new HashSet<>();

    private String reponseTexte;

    private Double reponseNumerique;

    private boolean correcte;

    private Double pointsObtenus;

    private String commentaireCorrecteur;
}