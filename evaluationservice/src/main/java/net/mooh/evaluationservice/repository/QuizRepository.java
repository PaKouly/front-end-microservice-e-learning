package net.mooh.evaluationservice.repository;

import net.mooh.evaluationservice.entities.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCreateurIdAndActifTrue(Long createurId);

    List<Quiz> findByFormationIdAndActifTrue(Long formationId);

    List<Quiz> findByModuleIdAndActifTrue(Long moduleId);

    List<Quiz> findByActifTrue();

    Page<Quiz> findByActifTrueOrderByDateCreationDesc(Pageable pageable);

    @Query("SELECT q FROM Quiz q WHERE q.actif = true AND " +
            "(LOWER(q.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(q.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Quiz> rechercherQuiz(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.quiz.id = :quizId")
    Integer countEvaluationsByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT AVG(e.noteObtenue) FROM Evaluation e WHERE e.quiz.id = :quizId AND e.statut = 'CORRIGEE'")
    Double getAverageNoteByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.quiz.id = :quizId AND e.statut = 'CORRIGEE'")
    Long countEvaluationsCorrigeesByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(e) FROM Evaluation e WHERE e.quiz.id = :quizId AND e.statut = 'CORRIGEE' AND e.reussie = true")
    Long countEvaluationsReussiesByQuizId(@Param("quizId") Long quizId);
}