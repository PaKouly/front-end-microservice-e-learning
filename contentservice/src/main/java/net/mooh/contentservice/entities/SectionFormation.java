package net.mooh.contentservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections_formation")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionFormation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String description;

    private Integer ordre;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    @ToString.Exclude
    private Formation formation;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<Contenu> contenus = new ArrayList<>();

    private Integer dureeEstimee; // en minutes

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Méthode utilitaire pour ajouter un contenu
    public void ajouterContenu(Contenu contenu) {
        contenus.add(contenu);
        contenu.setSection(this);
    }

    // Méthode utilitaire pour supprimer un contenu
    public void supprimerContenu(Contenu contenu) {
        contenus.remove(contenu);
        contenu.setSection(null);
    }
}
