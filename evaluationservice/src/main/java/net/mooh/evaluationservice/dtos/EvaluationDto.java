package net.mooh.evaluationservice.dtos;

import lombok.*;
import net.mooh.evaluationservice.entities.Evaluation.StatutEvaluation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationDto {

    private Long id;

    private Long quizId;

    private String quizTitre;

    private Long apprenantId;

    private String apprenantNom;

    private Integer tentative;

    private StatutEvaluation statut;

    private Double noteObtenue;

    private Double noteMaximale;

    private Double pourcentage;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private Integer dureeReelle;

    private boolean reussie;

    private List<ReponseApprenantDto> reponses = new ArrayList<>();

    private String commentaire;

    private Long correcteurId;

    private String correcteurNom;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;
}