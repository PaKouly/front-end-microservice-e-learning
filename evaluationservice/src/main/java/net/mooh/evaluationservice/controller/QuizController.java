package net.mooh.evaluationservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.evaluationservice.dtos.QuizDto;
import net.mooh.evaluationservice.service.QuizService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    public ResponseEntity<QuizDto> creerQuiz(@Valid @RequestBody QuizDto quizDto) {
        return new ResponseEntity<>(quizService.creerQuiz(quizDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDto> getQuizById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<QuizDto> getQuizDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(quizService.getQuizDetailById(id));
    }

    @GetMapping
    public ResponseEntity<Page<QuizDto>> getAllQuiz(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(quizService.getAllQuiz(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<QuizDto>> rechercherQuiz(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(quizService.rechercherQuiz(term, pageable));
    }

    @GetMapping("/createur/{createurId}")
    public ResponseEntity<List<QuizDto>> getQuizByCreateurId(@PathVariable Long createurId) {
        return ResponseEntity.ok(quizService.getQuizByCreateurId(createurId));
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<QuizDto>> getQuizByFormationId(@PathVariable Long formationId) {
        return ResponseEntity.ok(quizService.getQuizByFormationId(formationId));
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<QuizDto>> getQuizByModuleId(@PathVariable Long moduleId) {
        return ResponseEntity.ok(quizService.getQuizByModuleId(moduleId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizDto> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody QuizDto quizDto) {
        return ResponseEntity.ok(quizService.updateQuiz(id, quizDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activerQuiz(@PathVariable Long id) {
        quizService.activerQuiz(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiverQuiz(@PathVariable Long id) {
        quizService.desactiverQuiz(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/dupliquer")
    public ResponseEntity<QuizDto> duppliquerQuiz(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long nouveauCreateurId) {
        return ResponseEntity.ok(quizService.duppliquerQuiz(id, nouveauCreateurId));
    }
}