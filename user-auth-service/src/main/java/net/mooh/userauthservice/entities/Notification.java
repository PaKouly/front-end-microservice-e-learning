package net.mooh.userauthservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contenu;

    private String titre;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean lue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    // Types de notification
    public enum NotificationType {
        SYSTEME,
        FORMATION,
        FORUM,
        EVALUATION,
        CERTIFICATION
    }
}
