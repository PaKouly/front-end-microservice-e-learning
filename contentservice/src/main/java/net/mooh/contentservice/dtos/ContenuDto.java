package net.mooh.contentservice.dtos;

import lombok.*;
import net.mooh.contentservice.entities.Contenu;

import java.time.LocalDateTime;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContenuDto {


    private Long id;
    private String titre;

    private Contenu.TypeContenu type;

    private String url;

    private String texte;

    private Integer ordre;

    private String langue;

    private String format;

    private Long sectionId;

    private Integer dureeEstimee;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;
}
