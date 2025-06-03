package net.mooh.evaluationservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.evaluationservice.dtos.EvaluationDto;
import net.mooh.evaluationservice.dtos.PassageQuizDto;
import net.mooh.evaluationservice.dtos.SoumissionReponseDto;
import net.mooh.evaluationservice.service.EvaluationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/demarrer")
    public ResponseEntity<EvaluationDto> demarrerQuiz(@Valid @RequestBody PassageQuizDto passageQuizDto) {
        return new ResponseEntity<>(evaluationService.demarrerQuiz(passageQuizDto), HttpStatus.CREATED);
    }

    @PostMapping("/soumettre")
    public ResponseEntity<EvaluationDto> soumettreReponses(@Valid @RequestBody SoumissionReponseDto soumissionDto) {
        return ResponseEntity.ok(evaluationService.soumettreReponses(soumissionDto));
    }

    @PostMapping("/{evaluationId}/terminer")
    public ResponseEntity<EvaluationDto> terminerQuiz(@PathVariable Long evaluationId) {
        return ResponseEntity.ok(evaluationService.terminerQuiz(evaluationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationDto> getEvaluationById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.getEvaluationById(id));
    }

    @GetMapping("/apprenant/{apprenantId}")
    public ResponseEntity<List<EvaluationDto>> getEvaluationsByApprenantId(@PathVariable Long apprenantId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByApprenantId(apprenantId));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<EvaluationDto>> getEvaluationsByQuizId(@PathVariable Long quizId) {
        return ResponseEntity.ok(evaluationService.getEvaluationsByQuizId(quizId));
    }

    @GetMapping("/a-corriger")
    public ResponseEntity<Page<EvaluationDto>> getEvaluationsACorreiger(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(evaluationService.getEvaluationsACorreiger(pageable));
    }

    @PostMapping("/{id}/corriger")
    public ResponseEntity<EvaluationDto> corrigerEvaluation(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long correcteurId) {
        return ResponseEntity.ok(evaluationService.corrigerEvaluation(id, correcteurId));
    }

    @PatchMapping("/{id}/commentaire")
    public ResponseEntity<EvaluationDto> ajouterCommentaire(
            @PathVariable Long id,
            @RequestParam String commentaire,
            @RequestHeader("User-Id") Long correcteurId) {
        return ResponseEntity.ok(evaluationService.ajouterCommentaire(id, commentaire, correcteurId));
    }

    @GetMapping("/tentatives")
    public ResponseEntity<Integer> getNbTentatives(
            @RequestParam Long apprenantId,
            @RequestParam Long quizId) {
        return ResponseEntity.ok(evaluationService.getNbTentatives(apprenantId, quizId));
    }

    @GetMapping("/peut-passer")
    public ResponseEntity<Boolean> peutPasserQuiz(
            @RequestParam Long apprenantId,
            @RequestParam Long quizId) {
        return ResponseEntity.ok(evaluationService.peutPasserQuiz(apprenantId, quizId));
    }
}