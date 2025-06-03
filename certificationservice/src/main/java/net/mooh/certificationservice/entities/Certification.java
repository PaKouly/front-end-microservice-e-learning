package net.mooh.certificationservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "certifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Column(name = "formation_id")
    private Long formationId;

    @Column(name = "createur_id", nullable = false)
    private Long createurId;

    @Enumerated(EnumType.STRING)
    private TypeCertification type;

    private Double noteMinimaleRequise;

    private Integer dureeValidite; // en mois

    private boolean active;

    @ElementCollection
    @CollectionTable(name = "certification_prerequis", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "formation_id")
    private Set<Long> formationsPrerequisites = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "certification_quiz_requis", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "quiz_id")
    private Set<Long> quizRequis = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "certification_competences", joinColumns = @JoinColumn(name = "certification_id"))
    @Column(name = "competence")
    private Set<String> competences = new HashSet<>();

    @OneToMany(mappedBy = "certification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attestation> attestations = new ArrayList<>();

    @Column(length = 500)
    private String criteresValidation;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Types de certification
    public enum TypeCertification {
        FORMATION_COMPLETE,    // Certification pour une formation complète
        COMPETENCE_SPECIFIQUE, // Certification pour une compétence spécifique
        PARCOURS_PROFESSIONNEL, // Certification pour un parcours professionnel
        EVALUATION_CONTINUE,   // Certification basée sur évaluation continue
        EXAMEN_FINAL          // Certification basée sur un examen final
    }

    // Méthode utilitaire pour ajouter une formation prérequise
    public void ajouterFormationPrerequise(Long formationId) {
        if (formationsPrerequisites == null) {
            formationsPrerequisites = new HashSet<>();
        }
        formationsPrerequisites.add(formationId);
    }

    // Méthode utilitaire pour ajouter un quiz requis
    public void ajouterQuizRequis(Long quizId) {
        if (quizRequis == null) {
            quizRequis = new HashSet<>();
        }
        quizRequis.add(quizId);
    }

    // Méthode utilitaire pour ajouter une compétence
    public void ajouterCompetence(String competence) {
        if (competences == null) {
            competences = new HashSet<>();
        }
        competences.add(competence);
    }
}