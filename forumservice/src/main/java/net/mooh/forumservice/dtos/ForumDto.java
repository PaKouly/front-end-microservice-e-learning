package net.mooh.forumservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForumDto {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "L'ID de la formation est obligatoire")
    private Long formationId;

    private String formationTitre;

    private boolean actif;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    // Statistiques
    private Integer nbSujets;
    private Integer nbMessages;
    private Integer nbSignalementsNonResolus;
}
