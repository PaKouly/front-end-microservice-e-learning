package net.mooh.evaluationservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "apprenant_id", nullable = false)
    private Long apprenantId;

    @Column(name = "apprenant_nom")
    private String apprenantNom;

    private Integer tentative; // Numéro de la tentative

    @Enumerated(EnumType.STRING)
    private StatutEvaluation statut;

    private Double noteObtenue;

    private Double noteMaximale;

    private Double pourcentage;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private Integer dureeReelle; // en secondes

    private boolean reussie;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReponseApprenant> reponses = new ArrayList<>();

    @Column(length = 1000)
    private String commentaire;

    @Column(name = "correcteur_id")
    private Long correcteurId; // Pour correction manuelle

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Statuts d'évaluation
    public enum StatutEvaluation {
        EN_COURS,
        TERMINEE,
        CORRIGEE,
        ABANDONNEE
    }

    // Méthode utilitaire pour ajouter une réponse
    public void ajouterReponse(ReponseApprenant reponse) {
        reponses.add(reponse);
        reponse.setEvaluation(this);
    }

    // Méthode utilitaire pour calculer le pourcentage
    public void calculerPourcentage() {
        if (noteMaximale != null && noteMaximale > 0) {
            this.pourcentage = (noteObtenue / noteMaximale) * 100;
        }
    }

    // Méthode pour vérifier si l'évaluation est réussie
    public void verifierReussite() {
        if (quiz != null && quiz.getNoteMinimale() != null && pourcentage != null) {
            this.reussie = pourcentage >= quiz.getNoteMinimale();
        }
    }
}