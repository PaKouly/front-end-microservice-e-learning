package net.mooh.evaluationservice.dtos;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SoumissionReponseDto {

    @NotNull(message = "L'ID de l'évaluation est obligatoire")
    private Long evaluationId;

    private List<ReponseApprenantDto> reponses = new ArrayList<>();
}