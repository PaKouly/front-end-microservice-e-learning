package net.mooh.evaluationservice.dtos;

import lombok.*;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassageQuizDto {

    @NotNull(message = "L'ID du quiz est obligatoire")
    private Long quizId;

    @NotNull(message = "L'ID de l'apprenant est obligatoire")
    private Long apprenantId;
}