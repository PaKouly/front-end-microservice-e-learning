package net.mooh.forumservice.repository;

import net.mooh.forumservice.entities.Sujet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SujetRepository extends JpaRepository<Sujet, Long> {

    Page<Sujet> findByForumIdOrderByEpingleDescDateCreationDesc(Long forumId, Pageable pageable);

    List<Sujet> findByAuteurId(Long auteurId);

    @Query("SELECT s FROM Sujet s WHERE s.forum.id = :forumId AND " +
            "(LOWER(s.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(s.contenu) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Sujet> rechercherSujets(@Param("forumId") Long forumId,
                                 @Param("searchTerm") String searchTerm,
                                 Pageable pageable);

    Page<Sujet> findByForumIdAndResoluTrue(Long forumId, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Sujet s WHERE s.forum.id = :forumId")
    Integer countByForumId(@Param("forumId") Long forumId);
}
