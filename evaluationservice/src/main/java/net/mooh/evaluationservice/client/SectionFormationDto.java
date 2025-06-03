package net.mooh.evaluationservice.client;

import lombok.*;

import java.time.LocalDateTime;

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
    private Integer dureeEstimee;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
}