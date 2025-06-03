package net.mooh.evaluationservice.repository;

import net.mooh.evaluationservice.entities.Evaluation;
import net.mooh.evaluationservice.entities.Evaluation.StatutEvaluation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    List<Evaluation> findByApprenantIdOrderByDateCreationDesc(Long apprenantId);

    List<Evaluation> findByQuizIdOrderByDateCreationDesc(Long quizId);

    Optional<Evaluation> findByApprenantIdAndQuizIdAndStatut(Long apprenantId, Long quizId, StatutEvaluation statut);

    Page<Evaluation> findByQuizIdAndStatutOrderByDateCreationDesc(Long quizId, StatutEvaluation statut, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.apprenantId = :apprenantId AND e.quiz.id = :quizId")
    Integer countByApprenantIdAndQuizId(@Param("apprenantId") Long apprenantId, @Param("quizId") Long quizId);

    @Query("SELECT e FROM Evaluation e WHERE e.apprenantId = :apprenantId AND e.quiz.id = :quizId ORDER BY e.tentative DESC")
    List<Evaluation> findByApprenantIdAndQuizIdOrderByTentativeDesc(@Param("apprenantId") Long apprenantId, @Param("quizId") Long quizId);

    @Query("SELECT e FROM Evaluation e WHERE e.statut = 'TERMINEE' ORDER BY e.dateFin ASC")
    Page<Evaluation> findEvaluationsACorreiger(Pageable pageable);
}