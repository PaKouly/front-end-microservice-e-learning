package net.mooh.forumservice.repository;

import net.mooh.forumservice.entities.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findBySujetIdAndMessageParentIsNullOrderByDateCreationAsc(Long sujetId, Pageable pageable);

    List<Message> findByMessageParentIdOrderByDateCreationAsc(Long messageParentId);

    List<Message> findByAuteurId(Long auteurId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sujet.id = :sujetId")
    Integer countBySujetId(@Param("sujetId") Long sujetId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.sujet.forum.id = :forumId")
    Integer countByForumId(@Param("forumId") Long forumId);

    @Query("SELECT m FROM Message m WHERE m.sujet.id = :sujetId AND m.estValide = false ORDER BY m.dateCreation DESC")
    Page<Message> findNonValidatedMessagesBySujetId(@Param("sujetId") Long sujetId, Pageable pageable);
}
