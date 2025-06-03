package net.mooh.forumservice.dtos;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    private Long id;

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    @NotNull(message = "L'ID de l'auteur est obligatoire")
    private Long auteurId;

    private String auteurNom;

    private Long sujetId;

    private Long messageParentId;

    private List<MessageDto> reponses = new ArrayList<>();

    private boolean estValide;

    private boolean estModifie;

    private int nbLikes;

    private boolean likeParUtilisateur;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;
}
