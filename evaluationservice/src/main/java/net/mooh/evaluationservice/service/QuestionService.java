package net.mooh.evaluationservice.service;

import net.mooh.evaluationservice.dtos.QuestionDto;

import java.util.List;

public interface QuestionService {

    QuestionDto creerQuestion(Long quizId, QuestionDto questionDto);

    QuestionDto getQuestionById(Long id);

    List<QuestionDto> getQuestionsByQuizId(Long quizId);

    QuestionDto updateQuestion(Long id, QuestionDto questionDto);

    void deleteQuestion(Long id);

    void deplacerQuestion(Long id, Integer nouvelOrdre);
}