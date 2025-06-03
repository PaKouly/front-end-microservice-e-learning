package net.mooh.forumservice.dtos;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SujetDto {

    private Long id;

    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String contenu;

    @NotNull(message = "L'ID de l'auteur est obligatoire")
    private Long auteurId;

    private String auteurNom;

    private Long forumId;

    private boolean resolu;

    private boolean epingle;

    private boolean verrouille;

    private int nbVues;

    private int nbMessages;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    private MessageDto dernierMessage;
}
