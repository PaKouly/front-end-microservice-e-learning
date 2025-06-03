package net.mooh.certificationservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EvaluationClientFallback implements EvaluationClient {
    @Override
    public ResponseEntity<QuizDto> getQuizById(Long id) {
        return ResponseEntity.ok(QuizDto.builder()
                .id(id)
                .titre("Quiz indisponible")
                .build());
    }

    @Override
    public ResponseEntity<List<EvaluationDto>> getEvaluationsByApprenantId(Long apprenantId) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}