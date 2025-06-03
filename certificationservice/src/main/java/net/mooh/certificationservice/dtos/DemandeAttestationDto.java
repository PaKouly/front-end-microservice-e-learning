package net.mooh.certificationservice.dtos;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeAttestationDto {

    @NotNull(message = "L'ID de la certification est obligatoire")
    private Long certificationId;

    @NotNull(message = "L'ID du bénéficiaire est obligatoire")
    private Long beneficiaireId;

    private String commentaires;
}