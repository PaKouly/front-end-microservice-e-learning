package net.mooh.contentservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "progression_apprenants")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressionApprenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private Long apprenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id", nullable = false)
    @ToString.Exclude
    private Formation formation;

    @ElementCollection
    @CollectionTable(name = "progression_contenus", joinColumns = @JoinColumn(name = "progression_id"))
    @Column(name = "contenu_id")
    private Set<Long> contenusCompletes = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "progression_sections", joinColumns = @JoinColumn(name = "progression_id"))
    @Column(name = "section_id")
    private Set<Long> sectionsCompletees = new HashSet<>();

    // Pourcentage global d'achèvement de la formation
    private Integer pourcentageCompletion;

    // Dernière consultation de la formation
    private LocalDateTime derniereConsultation;

    @CreationTimestamp
    private LocalDateTime dateInscription;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    private boolean formationCompletee;

    // Méthode utilitaire pour marquer un contenu comme complété
    public void ajouterContenuComplete(Long contenuId) {
        this.contenusCompletes.add(contenuId);
        this.dateMiseAJour = LocalDateTime.now();
    }

    // Méthode utilitaire pour marquer une section comme complétée
    public void ajouterSectionCompletee(Long sectionId) {
        this.sectionsCompletees.add(sectionId);
        this.dateMiseAJour = LocalDateTime.now();
    }
}
