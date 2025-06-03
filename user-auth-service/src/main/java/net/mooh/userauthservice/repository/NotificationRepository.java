package net.mooh.userauthservice.repository;

import net.mooh.userauthservice.entities.Notification;
import net.mooh.userauthservice.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUtilisateurOrderByDateCreationDesc(Utilisateur utilisateur);

    List<Notification> findByUtilisateurAndLueOrderByDateCreationDesc(Utilisateur utilisateur, boolean lue);

    List<Notification> findByUtilisateurIdOrderByDateCreationDesc(Long utilisateurId);

    long countByUtilisateurAndLue(Utilisateur utilisateur, boolean lue);
}
