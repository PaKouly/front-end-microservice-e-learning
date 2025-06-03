package net.mooh.evaluationservice.repository;

import net.mooh.evaluationservice.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizIdOrderByOrdre(Long quizId);

    @Query("SELECT MAX(q.ordre) FROM Question q WHERE q.quiz.id = :quizId")
    Integer findMaxOrdreByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz.id = :quizId")
    Integer countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT SUM(q.points) FROM Question q WHERE q.quiz.id = :quizId")
    Integer calculateTotalPointsByQuizId(@Param("quizId") Long quizId);
}