package net.mooh.evaluationservice.dtos;

import lombok.*;
import net.mooh.evaluationservice.entities.Question.TypeQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDto {

    private Long id;

    @NotBlank(message = "L'énoncé est obligatoire")
    private String enonce;

    @NotNull(message = "Le type de question est obligatoire")
    private TypeQuestion type;

    private String explication;

    @Min(value = 1, message = "Les points doivent être d'au moins 1")
    private Integer points;

    private Integer ordre;

    private boolean obligatoire;

    private Long quizId;

    private List<ChoixReponseDto> choixReponses = new ArrayList<>();

    private String reponseTexte;

    private LocalDateTime dateCreation;

    private LocalDateTime dateMiseAJour;
}