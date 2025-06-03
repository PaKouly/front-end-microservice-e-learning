package net.mooh.forumservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5000)
    private String contenu;

    @Column(name = "auteur_id", nullable = false)
    private Long auteurId;

    @Column(name = "auteur_nom")
    private String auteurNom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sujet_id")
    private Sujet sujet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_parent_id")
    private Message messageParent;

    @OneToMany(mappedBy = "messageParent")
    @Builder.Default  // Important pour Lombok Builder
    private Set<Message> reponses = new HashSet<>();

    private boolean estValide;

    private boolean estModifie;

    // Utiliser @ElementCollection au lieu de @ManyToMany pour stocker des IDs
    @ElementCollection
    @CollectionTable(
            name = "message_likes",
            joinColumns = @JoinColumn(name = "message_id")
    )
    @Column(name = "utilisateur_id")
    @Builder.Default  // Important pour Lombok Builder
    private Set<Long> likes = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Méthode utilitaire pour ajouter une réponse
    public void ajouterReponse(Message reponse) {
        if (this.reponses == null) {
            this.reponses = new HashSet<>();
        }
        reponses.add(reponse);
        reponse.setMessageParent(this);
    }

    // Méthode utilitaire pour supprimer une réponse
    public void supprimerReponse(Message reponse) {
        if (this.reponses != null) {
            reponses.remove(reponse);
            reponse.setMessageParent(null);
        }
    }

    // Méthode utilitaire pour ajouter un like
    public boolean ajouterLike(Long utilisateurId) {
        if (this.likes == null) {
            this.likes = new HashSet<>();
        }
        return likes.add(utilisateurId);
    }

    // Méthode utilitaire pour supprimer un like
    public boolean supprimerLike(Long utilisateurId) {
        if (this.likes == null) {
            this.likes = new HashSet<>();
        }
        return likes.remove(utilisateurId);
    }

    // Méthode utilitaire pour compter les likes
    public int getNbLikes() {
        return likes != null ? likes.size() : 0;
    }

    // Getter avec vérification null
    public Set<Long> getLikes() {
        if (this.likes == null) {
            this.likes = new HashSet<>();
        }
        return this.likes;
    }

    // Getter avec vérification null pour les réponses
    public Set<Message> getReponses() {
        if (this.reponses == null) {
            this.reponses = new HashSet<>();
        }
        return this.reponses;
    }
}