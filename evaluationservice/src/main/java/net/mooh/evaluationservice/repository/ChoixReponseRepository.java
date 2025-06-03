package net.mooh.evaluationservice.repository;

import net.mooh.evaluationservice.entities.ChoixReponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoixReponseRepository extends JpaRepository<ChoixReponse, Long> {

    List<ChoixReponse> findByQuestionIdOrderByOrdre(Long questionId);

    @Query("SELECT c FROM ChoixReponse c WHERE c.question.id = :questionId AND c.correct = true")
    List<ChoixReponse> findCorrectChoicesByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(c) FROM ChoixReponse c WHERE c.question.id = :questionId")
    Integer countByQuestionId(@Param("questionId") Long questionId);
}