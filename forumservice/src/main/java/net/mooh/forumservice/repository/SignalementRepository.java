package net.mooh.forumservice.repository;

import net.mooh.forumservice.entities.Signalement;
import net.mooh.forumservice.entities.Signalement.StatutSignalement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignalementRepository extends JpaRepository<Signalement, Long> {

    Page<Signalement> findByStatutOrderByDateCreationDesc(StatutSignalement statut, Pageable pageable);

    List<Signalement> findByMessageIdOrderByDateCreationDesc(Long messageId);

    @Query("SELECT s FROM Signalement s WHERE s.message.sujet.forum.id = :forumId AND s.statut = :statut ORDER BY s.dateCreation DESC")
    Page<Signalement> findByForumIdAndStatut(@Param("forumId") Long forumId,
                                             @Param("statut") StatutSignalement statut,
                                             Pageable pageable);

    @Query("SELECT COUNT(s) FROM Signalement s WHERE s.message.sujet.forum.id = :forumId AND s.statut = :statut")
    Integer countByForumIdAndStatut(@Param("forumId") Long forumId, @Param("statut") StatutSignalement statut);
}
