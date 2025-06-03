package net.mooh.contentservice.repository;

import net.mooh.contentservice.entities.Contenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ContenuRepository extends JpaRepository<Contenu, Long> {

    List<Contenu> findBySectionIdOrderByOrdre(Long sectionId);

    @Query("SELECT MAX(c.ordre) FROM Contenu c WHERE c.section.id = :sectionId")
    Integer findMaxOrdreBySectionId(@Param("sectionId") Long sectionId);

    @Query("SELECT COUNT(c) FROM Contenu c WHERE c.section.id = :sectionId")
    Integer countBySectionId(@Param("sectionId") Long sectionId);

    @Query("SELECT SUM(c.dureeEstimee) FROM Contenu c WHERE c.section.id = :sectionId")
    Integer calculateTotalDurationBySectionId(@Param("sectionId") Long sectionId);

    List<Contenu> findByTypeAndSectionFormationId(Contenu.TypeContenu type, Long formationId);
}
