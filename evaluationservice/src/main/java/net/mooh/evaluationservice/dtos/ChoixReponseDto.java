package net.mooh.evaluationservice.dtos;

import lombok.*;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoixReponseDto {

    private Long id;

    @NotBlank(message = "Le texte du choix est obligatoire")
    private String texte;

    private boolean correct;

    private Integer ordre;

    private Long questionId;
}