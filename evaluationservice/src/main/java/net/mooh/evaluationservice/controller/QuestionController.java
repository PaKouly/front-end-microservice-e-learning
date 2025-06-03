package net.mooh.evaluationservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.evaluationservice.dtos.QuestionDto;
import net.mooh.evaluationservice.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping("/quiz/{quizId}")
    public ResponseEntity<QuestionDto> creerQuestion(
            @PathVariable Long quizId,
            @Valid @RequestBody QuestionDto questionDto) {
        return new ResponseEntity<>(questionService.creerQuestion(quizId, questionDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok(questionService.getQuestionById(id));
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionDto>> getQuestionsByQuizId(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.getQuestionsByQuizId(quizId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionDto questionDto) {
        return ResponseEntity.ok(questionService.updateQuestion(id, questionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deplacer")
    public ResponseEntity<Void> deplacerQuestion(
            @PathVariable Long id,
            @RequestParam Integer nouvelOrdre) {
        questionService.deplacerQuestion(id, nouvelOrdre);
        return ResponseEntity.ok().build();
    }
}