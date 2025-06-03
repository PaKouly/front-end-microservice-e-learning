package net.mooh.contentservice.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressionApprenantDto {
    private Long id;

    private Long apprenantId;

    private Long formationId;

    private String formationTitre;

    private Set<Long> contenusCompletes = new HashSet<>();

    private Set<Long> sectionsCompletees = new HashSet<>();

    private Integer pourcentageCompletion;

    private LocalDateTime derniereConsultation;

    private LocalDateTime dateInscription;

    private LocalDateTime dateMiseAJour;

    private boolean formationCompletee;
}
