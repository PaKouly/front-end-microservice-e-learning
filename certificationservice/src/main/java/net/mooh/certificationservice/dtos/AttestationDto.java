package net.mooh.certificationservice.dtos;

import lombok.*;
import net.mooh.certificationservice.entities.Attestation.StatutAttestation;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttestationDto {

    private Long id;

    private String numeroAttestation;

    private Long certificationId;

    private String certificationNom;

    private Long beneficiaireId;

    private String beneficiaireNom;

    private String beneficiaireEmail;

    private StatutAttestation statut;

    private LocalDateTime dateObtention;

    private LocalDateTime dateExpiration;

    private Double noteObtenue;

    private Double noteMaximale;

    private Double pourcentageReussite;

    private Long validateurId;

    private String validateurNom;

    private String commentaires;

    private String cheminFichierPdf;

    private String codeVerification;

    private boolean telecharge;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    // Propriétés calculées
    private boolean valide;
    private boolean expiree;
    private Long joursRestantsAvantExpiration;
}