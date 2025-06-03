package net.mooh.forumservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import net.mooh.forumservice.entities.Signalement.StatutSignalement;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignalementDto {

    private Long id;

    @NotNull(message = "L'ID du message est obligatoire")
    private Long messageId;

    private MessageDto message;

    @NotNull(message = "L'ID du signaleur est obligatoire")
    private Long signaleurId;

    private String signaleurNom;

    @NotBlank(message = "La raison est obligatoire")
    private String raison;

    private String description;

    private StatutSignalement statut;

    private String commentaireModerateurr;

    private Long moderateurId;

    private String moderateurNom;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;
}
