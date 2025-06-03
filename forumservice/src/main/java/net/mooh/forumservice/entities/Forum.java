package net.mooh.forumservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "forums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Forum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(name = "formation_id", nullable = false)
    private Long formationId;

    private boolean actif;

    @OneToMany(mappedBy = "forum", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sujet> sujets = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Méthode utilitaire pour ajouter un sujet
    public void ajouterSujet(Sujet sujet) {
        sujets.add(sujet);
        sujet.setForum(this);
    }

    // Méthode utilitaire pour supprimer un sujet
    public void supprimerSujet(Sujet sujet) {
        sujets.remove(sujet);
        sujet.setForum(null);
    }
}
