package net.mooh.certificationservice.client;

import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizDto {
    private Long id;
    private String titre;
    private String description;
    private Long formationId;
    private Long moduleId;
    private Long createurId;
    private Integer duree;
    private Integer noteMinimale;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
}