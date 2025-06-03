package net.mooh.certificationservice.dtos;

import lombok.*;
import net.mooh.certificationservice.entities.Attestation.StatutAttestation;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationAttestationDto {

    @NotNull(message = "L'ID de l'attestation est obligatoire")
    private Long attestationId;

    @NotNull(message = "Le statut est obligatoire")
    private StatutAttestation statut;

    private String commentaires;

    private Double noteObtenue;

    private Double noteMaximale;
}