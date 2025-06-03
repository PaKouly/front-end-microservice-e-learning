package net.mooh.evaluationservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "reponses_apprenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReponseApprenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ElementCollection
    @CollectionTable(name = "reponse_choix_selectionnes", joinColumns = @JoinColumn(name = "reponse_id"))
    @Column(name = "choix_id")
    private Set<Long> choixSelectionnes = new HashSet<>();

    @Column(length = 2000)
    private String reponseTexte;

    private Double reponseNumerique;

    private boolean correcte;

    private Double pointsObtenus;

    @Column(length = 1000)
    private String commentaireCorrecteur;

    // Méthode utilitaire pour ajouter un choix sélectionné
    public void ajouterChoixSelectionne(Long choixId) {
        if (choixSelectionnes == null) {
            choixSelectionnes = new HashSet<>();
        }
        choixSelectionnes.add(choixId);
    }

    // Méthode utilitaire pour supprimer un choix sélectionné
    public void supprimerChoixSelectionne(Long choixId) {
        if (choixSelectionnes != null) {
            choixSelectionnes.remove(choixId);
        }
    }
}