package net.mooh.evaluationservice.service;

import net.mooh.evaluationservice.dtos.EvaluationDto;
import net.mooh.evaluationservice.dtos.PassageQuizDto;
import net.mooh.evaluationservice.dtos.SoumissionReponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EvaluationService {

    EvaluationDto demarrerQuiz(PassageQuizDto passageQuizDto);

    EvaluationDto soumettreReponses(SoumissionReponseDto soumissionDto);

    EvaluationDto terminerQuiz(Long evaluationId);

    EvaluationDto getEvaluationById(Long id);

    List<EvaluationDto> getEvaluationsByApprenantId(Long apprenantId);

    List<EvaluationDto> getEvaluationsByQuizId(Long quizId);

    Page<EvaluationDto> getEvaluationsACorreiger(Pageable pageable);

    EvaluationDto corrigerEvaluation(Long id, Long correcteurId);

    EvaluationDto ajouterCommentaire(Long id, String commentaire, Long correcteurId);

    Integer getNbTentatives(Long apprenantId, Long quizId);

    boolean peutPasserQuiz(Long apprenantId, Long quizId);
}