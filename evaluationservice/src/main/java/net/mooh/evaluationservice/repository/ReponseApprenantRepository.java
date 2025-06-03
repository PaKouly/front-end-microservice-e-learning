package net.mooh.evaluationservice.repository;

import net.mooh.evaluationservice.entities.ReponseApprenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReponseApprenantRepository extends JpaRepository<ReponseApprenant, Long> {

    List<ReponseApprenant> findByEvaluationId(Long evaluationId);

    Optional<ReponseApprenant> findByEvaluationIdAndQuestionId(Long evaluationId, Long questionId);

    @Query("SELECT SUM(r.pointsObtenus) FROM ReponseApprenant r WHERE r.evaluation.id = :evaluationId")
    Double calculateTotalPointsByEvaluationId(@Param("evaluationId") Long evaluationId);
}