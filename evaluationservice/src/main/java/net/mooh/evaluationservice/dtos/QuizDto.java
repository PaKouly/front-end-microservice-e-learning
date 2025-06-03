package net.mooh.evaluationservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDto {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    private Long formationId;

    private String formationTitre;

    private Long moduleId;

    private String moduleTitre;

    @NotNull(message = "L'ID du créateur est obligatoire")
    private Long createurId;

    private String createurNom;

    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private Integer duree;

    @Min(value = 0, message = "La note minimale ne peut pas être négative")
    private Integer noteMinimale;

    @Min(value = 1, message = "Le nombre de tentatives doit être d'au moins 1")
    private Integer nombreTentativesMax;

    private boolean melangQuestions;

    private boolean melangReponses;

    private boolean affichageResultatImmediat;

    private boolean actif;

    private List<QuestionDto> questions = new ArrayList<>();

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    // Statistiques
    private Integer nbQuestions;
    private Integer totalPoints;
    private Integer nbEvaluations;
    private Double moyenneNotes;
    private Double tauxReussite;
}