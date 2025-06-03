package net.mooh.certificationservice.client;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgressionApprenantDto {
    private Long id;
    private Long apprenantId;
    private Long formationId;
    private Integer pourcentageCompletion;
    private boolean formationCompletee;
    private LocalDateTime dateInscription;
    private LocalDateTime dateMiseAJour;
}