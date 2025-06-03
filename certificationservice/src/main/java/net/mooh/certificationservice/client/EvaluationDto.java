package net.mooh.certificationservice.client;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluationDto {
    private Long id;
    private Long quizId;
    private Long apprenantId;
    private Integer tentative;
    private Double noteObtenue;
    private Double noteMaximale;
    private Double pourcentage;
    private boolean reussie;
    private LocalDateTime dateCreation;
}