package net.mooh.userauthservice.repository;

import net.mooh.userauthservice.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM Utilisateur u JOIN u.roles r WHERE r.nom = :roleName")
    List<Utilisateur> findByRoleName(String roleName);

    List<Utilisateur> findByNomContainingOrPrenomContaining(String nom, String prenom);

    @Query("SELECT u FROM Utilisateur u WHERE u.actif = true")
    List<Utilisateur> findAllActiveUsers();
}
