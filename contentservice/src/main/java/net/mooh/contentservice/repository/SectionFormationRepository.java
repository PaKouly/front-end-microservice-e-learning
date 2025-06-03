package net.mooh.contentservice.repository;

import net.mooh.contentservice.entities.SectionFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SectionFormationRepository extends JpaRepository<SectionFormation, Long> {
    List<SectionFormation> findByFormationIdOrderByOrdre(Long formationId);

    @Query("SELECT MAX(s.ordre) FROM SectionFormation s WHERE s.formation.id = :formationId")
    Integer findMaxOrdreByFormationId(@Param("formationId") Long formationId);

    @Query("SELECT COUNT(s) FROM SectionFormation s WHERE s.formation.id = :formationId")
    Integer countByFormationId(@Param("formationId") Long formationId);

    @Query("SELECT SUM(s.dureeEstimee) FROM SectionFormation s WHERE s.formation.id = :formationId")
    Integer calculateTotalDurationByFormationId(@Param("formationId") Long formationId);
}
