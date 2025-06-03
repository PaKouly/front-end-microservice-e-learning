package net.mooh.certificationservice.dtos;

import lombok.*;
import net.mooh.certificationservice.entities.Certification.TypeCertification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificationDto {

    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;

    private Long formationId;

    private String formationTitre;

    @NotNull(message = "L'ID du créateur est obligatoire")
    private Long createurId;

    private String createurNom;

    @NotNull(message = "Le type de certification est obligatoire")
    private TypeCertification type;

    @Min(value = 0, message = "La note minimale ne peut pas être négative")
    private Double noteMinimaleRequise;

    @Min(value = 1, message = "La durée de validité doit être d'au moins 1 mois")
    private Integer dureeValidite;

    private boolean active;

    private Set<Long> formationsPrerequisites = new HashSet<>();

    private Set<String> formationsPrerequisitesTitres = new HashSet<>();

    private Set<Long> quizRequis = new HashSet<>();

    private Set<String> quizRequisTitres = new HashSet<>();

    private Set<String> competences = new HashSet<>();

    private String criteresValidation;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    // Statistiques
    private Integer nbAttestationsDelivrees;
    private Integer nbAttestationsActives;
    private Double tauxReussite;
}