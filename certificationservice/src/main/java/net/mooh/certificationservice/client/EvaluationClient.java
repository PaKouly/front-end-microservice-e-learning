package net.mooh.certificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "evaluationservice", fallback = EvaluationClientFallback.class)
public interface EvaluationClient {
    @GetMapping("/api/quiz/{id}")
    ResponseEntity<QuizDto> getQuizById(@PathVariable Long id);

    @GetMapping("/api/evaluations/apprenant/{apprenantId}")
    ResponseEntity<List<EvaluationDto>> getEvaluationsByApprenantId(@PathVariable Long apprenantId);
}