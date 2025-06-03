package net.mooh.evaluationservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String enonce;

    @Enumerated(EnumType.STRING)
    private TypeQuestion type;

    @Column(length = 1000)
    private String explication;

    private Integer points;

    private Integer ordre;

    private boolean obligatoire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<ChoixReponse> choixReponses = new ArrayList<>();

    @Column(length = 2000)
    private String reponseTexte; // Pour les questions à réponse libre

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Types de questions
    public enum TypeQuestion {
        QCM_UNIQUE,        // Choix multiple - une seule réponse
        QCM_MULTIPLE,      // Choix multiple - plusieurs réponses possibles
        VRAI_FAUX,         // Question vrai/faux
        TEXTE_LIBRE,       // Réponse textuelle libre
        NUMERIQUE,         // Réponse numérique
        CORRESPONDANCE,    // Associer des éléments
        ORDONNANCEMENT     // Mettre en ordre
    }

    // Méthode utilitaire pour ajouter un choix de réponse
    public void ajouterChoixReponse(ChoixReponse choixReponse) {
        choixReponses.add(choixReponse);
        choixReponse.setQuestion(this);
    }

    // Méthode utilitaire pour supprimer un choix de réponse
    public void supprimerChoixReponse(ChoixReponse choixReponse) {
        choixReponses.remove(choixReponse);
        choixReponse.setQuestion(null);
    }
}