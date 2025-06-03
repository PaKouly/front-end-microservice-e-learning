package net.mooh.contentservice.repository;

import net.mooh.contentservice.entities.Formation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    List<Formation> findByCreateurId(Long createurId);

    List<Formation> findByActiveTrue();

    Page<Formation> findByActiveTrueOrderByDateCreationDesc(Pageable pageable);

    @Query("SELECT f FROM Formation f WHERE f.active = true AND " +
            "(LOWER(f.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(f.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Formation> rechercherFormations(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT f FROM Formation f JOIN f.tags tag WHERE f.active = true AND LOWER(tag) = LOWER(:tag)")
    List<Formation> findByTag(@Param("tag") String tag);

    List<Formation> findByNiveauAndActiveTrue(String niveau);

    List<Formation> findByLangueAndActiveTrue(String langue);

    @Query("SELECT DISTINCT f.niveau FROM Formation f WHERE f.active = true ORDER BY f.niveau")
    List<String> findAllNiveaux();

    @Query("SELECT DISTINCT f.langue FROM Formation f WHERE f.active = true ORDER BY f.langue")
    List<String> findAllLangues();

    @Query("SELECT DISTINCT tag FROM Formation f JOIN f.tags tag WHERE f.active = true ORDER BY tag")
    List<String> findAllTags();
}
