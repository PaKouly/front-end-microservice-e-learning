package net.mooh.forumservice.repository;

import net.mooh.forumservice.entities.Forum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForumRepository extends JpaRepository<Forum, Long> {

    Optional<Forum> findByFormationId(Long formationId);

    List<Forum> findByActifTrue();

    boolean existsByFormationId(Long formationId);
}