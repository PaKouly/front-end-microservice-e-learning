package net.mooh.forumservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "signalements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signalement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id")
    private Message message;

    @Column(name = "signaleur_id", nullable = false)
    private Long signaleurId;

    @Column(nullable = false)
    private String raison;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private StatutSignalement statut;

    private String commentaireModerateurr;

    @Column(name = "moderateur_id")
    private Long moderateurId;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    public enum StatutSignalement {
        NOUVEAU,
        EN_COURS,
        RESOLU,
        REJETE
    }
}
