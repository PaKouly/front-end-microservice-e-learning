package net.mooh.certificationservice.service;

import net.mooh.certificationservice.dtos.AttestationDto;
import net.mooh.certificationservice.dtos.DemandeAttestationDto;
import net.mooh.certificationservice.dtos.ValidationAttestationDto;
import net.mooh.certificationservice.entities.Attestation.StatutAttestation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttestationService {

    AttestationDto demanderAttestation(DemandeAttestationDto demandeDto);

    AttestationDto validerAttestation(ValidationAttestationDto validationDto, Long validateurId);

    AttestationDto getAttestationById(Long id);

    AttestationDto getAttestationByNumero(String numeroAttestation);

    AttestationDto verifierAttestation(String codeVerification);

    List<AttestationDto> getAttestationsByBeneficiaire(Long beneficiaireId);

    List<AttestationDto> getAttestationsByCertification(Long certificationId);

    Page<AttestationDto> getAttestationsByStatut(StatutAttestation statut, Pageable pageable);

    AttestationDto updateAttestation(Long id, AttestationDto attestationDto);

    void revoquerAttestation(Long id, String motif, Long validateurId);

    void suspendreAttestation(Long id, String motif, Long validateurId);

    void reactiverAttestation(Long id, Long validateurId);

    byte[] genererPdfAttestation(Long id);

    void marquerCommeTelecharge(Long id);

    List<AttestationDto> getAttestationsExpirantBientot(int nombreJours);

    void traiterAttestationsExpirees();

    String genererNumeroAttestation();

    String genererCodeVerification();
}