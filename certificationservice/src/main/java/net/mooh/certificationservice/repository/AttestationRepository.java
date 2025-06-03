package net.mooh.certificationservice.repository;

import net.mooh.certificationservice.entities.Attestation;
import net.mooh.certificationservice.entities.Attestation.StatutAttestation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttestationRepository extends JpaRepository<Attestation, Long> {

    List<Attestation> findByBeneficiaireIdOrderByDateCreationDesc(Long beneficiaireId);

    List<Attestation> findByCertificationIdOrderByDateCreationDesc(Long certificationId);

    Page<Attestation> findByStatutOrderByDateCreationDesc(StatutAttestation statut, Pageable pageable);

    Optional<Attestation> findByNumeroAttestation(String numeroAttestation);

    Optional<Attestation> findByCodeVerification(String codeVerification);

    Optional<Attestation> findByBeneficiaireIdAndCertificationId(Long beneficiaireId, Long certificationId);

    @Query("SELECT a FROM Attestation a WHERE a.dateExpiration < :date AND a.statut = 'VALIDEE'")
    List<Attestation> findAttestationsExpirees(@Param("date") LocalDateTime date);

    @Query("SELECT a FROM Attestation a WHERE a.dateExpiration BETWEEN :startDate AND :endDate AND a.statut = 'VALIDEE'")
    List<Attestation> findAttestationsExpirantBientot(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(a) FROM Attestation a WHERE a.beneficiaireId = :beneficiaireId AND a.statut = 'VALIDEE'")
    Integer countAttestationsValidesByBeneficiaire(@Param("beneficiaireId") Long beneficiaireId);

    boolean existsByNumeroAttestation(String numeroAttestation);
}