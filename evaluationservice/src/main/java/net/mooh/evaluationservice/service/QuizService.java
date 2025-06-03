package net.mooh.evaluationservice.service;

import net.mooh.evaluationservice.dtos.QuizDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuizService {

    QuizDto creerQuiz(QuizDto quizDto);

    QuizDto getQuizById(Long id);

    QuizDto getQuizDetailById(Long id);

    Page<QuizDto> getAllQuiz(Pageable pageable);

    Page<QuizDto> rechercherQuiz(String searchTerm, Pageable pageable);

    List<QuizDto> getQuizByCreateurId(Long createurId);

    List<QuizDto> getQuizByFormationId(Long formationId);

    List<QuizDto> getQuizByModuleId(Long moduleId);

    QuizDto updateQuiz(Long id, QuizDto quizDto);

    void deleteQuiz(Long id);

    void activerQuiz(Long id);

    void desactiverQuiz(Long id);

    QuizDto duppliquerQuiz(Long id, Long nouveauCreateurId);
}