package net.mooh.contentservice.entities;

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
@Table(name = "formations")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Formation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private String description;

    private String niveau;

    private Integer duree;

    private String langue;

    private boolean publique;

    private boolean active;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<SectionFormation> sections = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "formation_prerequis", joinColumns = @JoinColumn(name = "formation_id"))
    @Column(name = "prerequis")
    private Set<String> prerequis = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "formation_objectifs", joinColumns = @JoinColumn(name = "formation_id"))
    @Column(name = "objectif")
    private Set<String> objectifs = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "formation_tags", joinColumns = @JoinColumn(name = "formation_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @Column(name = "createur_id")
    private Long createurId;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Méthode utilitaire pour ajouter une section
    public void ajouterSection(SectionFormation section) {
        sections.add(section);
        section.setFormation(this);
    }

    // Méthode utilitaire pour supprimer une section
    public void supprimerSection(SectionFormation section) {
        sections.remove(section);
        section.setFormation(null);
    }
}
