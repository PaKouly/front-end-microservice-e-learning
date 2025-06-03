package net.mooh.certificationservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "attestations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attestation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroAttestation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @Column(name = "beneficiaire_id", nullable = false)
    private Long beneficiaireId;

    @Column(name = "beneficiaire_nom")
    private String beneficiaireNom;

    @Column(name = "beneficiaire_email")
    private String beneficiaireEmail;

    @Enumerated(EnumType.STRING)
    private StatutAttestation statut;

    private LocalDateTime dateObtention;

    private LocalDateTime dateExpiration;

    private Double noteObtenue;

    private Double noteMaximale;

    private Double pourcentageReussite;

    @Column(name = "validateur_id")
    private Long validateurId;

    @Column(name = "validateur_nom")
    private String validateurNom;

    @Column(length = 1000)
    private String commentaires;

    @Column(length = 500)
    private String cheminFichierPdf;

    @Column(length = 100)
    private String codeVerification;

    private boolean telecharge;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Statuts d'attestation
    public enum StatutAttestation {
        EN_ATTENTE,      // En attente de validation
        VALIDEE,         // Attestation validée et active
        EXPIREE,         // Attestation expirée
        REVOQUEE,        // Attestation révoquée
        SUSPENDUE        // Attestation temporairement suspendue
    }

    // Méthode utilitaire pour vérifier si l'attestation est valide
    public boolean isValide() {
        return statut == StatutAttestation.VALIDEE &&
                (dateExpiration == null || dateExpiration.isAfter(LocalDateTime.now()));
    }

    // Méthode utilitaire pour vérifier si l'attestation est expirée
    public boolean isExpiree() {
        return dateExpiration != null && dateExpiration.isBefore(LocalDateTime.now());
    }

    // Méthode utilitaire pour calculer les jours restants avant expiration
    public Long getJoursRestantsAvantExpiration() {
        if (dateExpiration == null) {
            return null; // Pas d'expiration
        }
        long joursRestants = java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), dateExpiration);
        return Math.max(0, joursRestants);
    }
}