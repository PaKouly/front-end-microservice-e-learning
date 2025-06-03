package net.mooh.contentservice.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionFormationDto {
    private Long id;
    private String titre;

    private String description;

    private Integer ordre;

    private Long formationId;

    private List<ContenuDto> contenus = new ArrayList<>();

    private Integer dureeEstimee;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;

    // Statistiques supplémentaires
    private Integer nbContenus;
}
