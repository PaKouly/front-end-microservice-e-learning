package net.mooh.contentservice.repository;

import net.mooh.contentservice.entities.ProgressionApprenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressionApprenantRepository extends JpaRepository<ProgressionApprenant, Long> {
    Optional<ProgressionApprenant> findByApprenantIdAndFormationId(Long apprenantId, Long formationId);

    List<ProgressionApprenant> findByApprenantId(Long apprenantId);

    List<ProgressionApprenant> findByFormationId(Long formationId);

    @Query("SELECT AVG(p.pourcentageCompletion) FROM ProgressionApprenant p WHERE p.formation.id = :formationId")
    Double getAverageCompletionByFormationId(@Param("formationId") Long formationId);

    @Query("SELECT COUNT(p) FROM ProgressionApprenant p WHERE p.formation.id = :formationId")
    Integer countByFormationId(@Param("formationId") Long formationId);

    @Query("SELECT COUNT(p) FROM ProgressionApprenant p WHERE p.formation.id = :formationId AND p.formationCompletee = true")
    Integer countCompletedByFormationId(@Param("formationId") Long formationId);
}
