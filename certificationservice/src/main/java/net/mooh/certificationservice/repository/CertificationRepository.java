package net.mooh.certificationservice.repository;

import net.mooh.certificationservice.entities.Certification;
import net.mooh.certificationservice.entities.Certification.TypeCertification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByCreateurIdAndActiveTrue(Long createurId);

    List<Certification> findByFormationIdAndActiveTrue(Long formationId);

    List<Certification> findByTypeAndActiveTrue(TypeCertification type);

    List<Certification> findByActiveTrue();

    Page<Certification> findByActiveTrueOrderByDateCreationDesc(Pageable pageable);

    @Query("SELECT c FROM Certification c WHERE c.active = true AND " +
            "(LOWER(c.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Certification> rechercherCertifications(@Param("searchTerm") String searchTerm, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Attestation a WHERE a.certification.id = :certificationId")
    Integer countAttestationsByCertificationId(@Param("certificationId") Long certificationId);

    @Query("SELECT COUNT(a) FROM Attestation a WHERE a.certification.id = :certificationId AND a.statut = 'VALIDEE'")
    Integer countAttestationsActivesByCertificationId(@Param("certificationId") Long certificationId);

    boolean existsByNom(String nom);
}