package net.mooh.forumservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sujets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sujet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String contenu;

    @Column(name = "auteur_id", nullable = false)
    private Long auteurId;

    @Column(name = "auteur_nom")
    private String auteurNom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    private Forum forum;

    private boolean resolu;

    private boolean epingle;

    private boolean verrouille;

    @OneToMany(mappedBy = "sujet", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dateCreation ASC")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "nb_vues")
    private int nbVues;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Méthode utilitaire pour ajouter un message
    public void ajouterMessage(Message message) {
        messages.add(message);
        message.setSujet(this);
    }

    // Méthode utilitaire pour supprimer un message
    public void supprimerMessage(Message message) {
        messages.remove(message);
        message.setSujet(null);
    }

    // Méthode utilitaire pour incrémenter les vues
    public void incrementerVues() {
        this.nbVues++;
    }
}
