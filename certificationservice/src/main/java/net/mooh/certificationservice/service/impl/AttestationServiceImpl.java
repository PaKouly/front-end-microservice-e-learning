package net.mooh.certificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.certificationservice.client.UserClient;
import net.mooh.certificationservice.client.UserDto;
import net.mooh.certificationservice.dtos.AttestationDto;
import net.mooh.certificationservice.dtos.DemandeAttestationDto;
import net.mooh.certificationservice.dtos.ValidationAttestationDto;
import net.mooh.certificationservice.entities.Attestation;
import net.mooh.certificationservice.entities.Attestation.StatutAttestation;
import net.mooh.certificationservice.entities.Certification;
import net.mooh.certificationservice.exception.ResourceNotFoundException;
import net.mooh.certificationservice.exception.UnauthorizedException;
import net.mooh.certificationservice.repository.AttestationRepository;
import net.mooh.certificationservice.repository.CertificationRepository;
import net.mooh.certificationservice.service.AttestationService;
import net.mooh.certificationservice.service.CertificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttestationServiceImpl implements AttestationService {

    private final AttestationRepository attestationRepository;
    private final CertificationRepository certificationRepository;
    private final CertificationService certificationService;
    @Qualifier("net.mooh.certificationservice.client.UserClient")
    private final UserClient userClient;

    @Override
    @Transactional
    public AttestationDto demanderAttestation(DemandeAttestationDto demandeDto) {
        // Vérifier que la certification existe et est active
        Certification certification = certificationRepository.findById(demandeDto.getCertificationId())
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", demandeDto.getCertificationId()));

        if (!certification.isActive()) {
            throw new UnauthorizedException("Cette certification n'est pas active");
        }

        // Vérifier que l'utilisateur existe
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(demandeDto.getBeneficiaireId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", demandeDto.getBeneficiaireId());
        }

        UserDto user = userResponse.getBody();

        // Vérifier que l'utilisateur peut obtenir cette certification
        if (!certificationService.peutObtenirCertification(demandeDto.getBeneficiaireId(), demandeDto.getCertificationId())) {
            throw new UnauthorizedException("Les prérequis pour cette certification ne sont pas remplis");
        }

        // Vérifier qu'il n'y a pas déjà une attestation active
        if (attestationRepository.findByBeneficiaireIdAndCertificationId(
                demandeDto.getBeneficiaireId(), demandeDto.getCertificationId()).isPresent()) {
            throw new UnauthorizedException("Une attestation existe déjà pour cette certification");
        }

        // Créer l'attestation
        Attestation attestation = Attestation.builder()
                .numeroAttestation(genererNumeroAttestation())
                .certification(certification)
                .beneficiaireId(demandeDto.getBeneficiaireId())
                .beneficiaireNom(user.getNom() + " " + user.getPrenom())
                .beneficiaireEmail(user.getEmail())
                .statut(StatutAttestation.EN_ATTENTE)
                .commentaires(demandeDto.getCommentaires())
                .codeVerification(genererCodeVerification())
                .telecharge(false)
                .build();

        // Calculer la date d'expiration si durée de validité définie
        if (certification.getDureeValidite() != null) {
            attestation.setDateExpiration(LocalDateTime.now().plusMonths(certification.getDureeValidite()));
        }

        Attestation savedAttestation = attestationRepository.save(attestation);
        return mapToDto(savedAttestation);
    }

    @Override
    @Transactional
    public AttestationDto validerAttestation(ValidationAttestationDto validationDto, Long validateurId) {
        // Vérifier que le validateur a les droits
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(validateurId);
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", validateurId);
        }

        UserDto user = userResponse.getBody();
        if (!user.getRoles().contains("FORMATEUR") && !user.getRoles().contains("ADMINISTRATEUR")) {
            throw new UnauthorizedException("L'utilisateur n'a pas le rôle nécessaire pour valider une attestation");
        }

        Attestation attestation = attestationRepository.findById(validationDto.getAttestationId())
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", validationDto.getAttestationId()));

        if (attestation.getStatut() != StatutAttestation.EN_ATTENTE) {
            throw new UnauthorizedException("Cette attestation ne peut plus être modifiée");
        }

        // Mettre à jour l'attestation
        attestation.setStatut(validationDto.getStatut());
        attestation.setValidateurId(validateurId);
        attestation.setValidateurNom(user.getNom() + " " + user.getPrenom());
        attestation.setCommentaires(validationDto.getCommentaires());

        if (validationDto.getNoteObtenue() != null) {
            attestation.setNoteObtenue(validationDto.getNoteObtenue());
        }
        if (validationDto.getNoteMaximale() != null) {
            attestation.setNoteMaximale(validationDto.getNoteMaximale());
            if (attestation.getNoteObtenue() != null) {
                attestation.setPourcentageReussite(
                        (attestation.getNoteObtenue() / attestation.getNoteMaximale()) * 100);
            }
        }

        if (validationDto.getStatut() == StatutAttestation.VALIDEE) {
            attestation.setDateObtention(LocalDateTime.now());
        }

        Attestation updatedAttestation = attestationRepository.save(attestation);
        return mapToDto(updatedAttestation);
    }

    @Override
    public AttestationDto getAttestationById(Long id) {
        Attestation attestation = attestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", id));
        return mapToDto(attestation);
    }

    @Override
    public AttestationDto getAttestationByNumero(String numeroAttestation) {
        Attestation attestation = attestationRepository.findByNumeroAttestation(numeroAttestation)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "numero", numeroAttestation));
        return mapToDto(attestation);
    }

    @Override
    public AttestationDto verifierAttestation(String codeVerification) {
        Attestation attestation = attestationRepository.findByCodeVerification(codeVerification)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "code de vérification", codeVerification));
        return mapToDto(attestation);
    }

    @Override
    public List<AttestationDto> getAttestationsByBeneficiaire(Long beneficiaireId) {
        return attestationRepository.findByBeneficiaireIdOrderByDateCreationDesc(beneficiaireId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttestationDto> getAttestationsByCertification(Long certificationId) {
        return attestationRepository.findByCertificationIdOrderByDateCreationDesc(certificationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<AttestationDto> getAttestationsByStatut(StatutAttestation statut, Pageable pageable) {
        return attestationRepository.findByStatutOrderByDateCreationDesc(statut, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public AttestationDto updateAttestation(Long id, AttestationDto attestationDto) {
        Attestation attestation = attestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", id));

        // Seuls certains champs peuvent être mis à jour
        attestation.setCommentaires(attestationDto.getCommentaires());

        Attestation updatedAttestation = attestationRepository.save(attestation);
        return mapToDto(updatedAttestation);
    }

    @Override
    @Transactional
    public void revoquerAttestation(Long id, String motif, Long validateurId) {
        Attestation attestation = attestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", id));

        attestation.setStatut(StatutAttestation.REVOQUEE);
        attestation.setCommentaires(motif);
        attestation.setValidateurId(validateurId);

        attestationRepository.save(attestation);
    }

    @Override
    @Transactional
    public void suspendreAttestation(Long id, String motif, Long validateurId) {
        Attestation attestation = attestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", id));

        attestation.setStatut(StatutAttestation.SUSPENDUE);
        attestation.setCommentaires(motif);
        attestation.setValidateurId(validateurId);

        attestationRepository.save(attestation);
    }

    @Override
    @Transactional
    public void reactiverAttestation(Long id, Long validateurId) {
        Attestation attestation = attestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", id));

        if (attestation.getStatut() == StatutAttestation.SUSPENDUE) {
            attestation.setStatut(StatutAttestation.VALIDEE);
            attestation.setValidateurId(validateurId);
            attestationRepository.save(attestation);
        }
    }

    @Override
    public byte[] genererPdfAttestation(Long id) {
        Attestation attestation = attestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", id));

        if (!attestation.isValide()) {
            throw new UnauthorizedException("Cette attestation n'est pas valide");
        }

        // TODO: Implémentation de la génération PDF
        // Utiliser une bibliothèque comme iText ou Apache PDFBox
        // Générer le PDF avec les informations de l'attestation

        return new byte[0]; // Placeholder
    }

    @Override
    @Transactional
    public void marquerCommeTelecharge(Long id) {
        Attestation attestation = attestationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attestation", "id", id));

        attestation.setTelecharge(true);
        attestationRepository.save(attestation);
    }

    @Override
    public List<AttestationDto> getAttestationsExpirantBientot(int nombreJours) {
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = startDate.plusDays(nombreJours);

        return attestationRepository.findAttestationsExpirantBientot(startDate, endDate).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void traiterAttestationsExpirees() {
        List<Attestation> attestationsExpirees = attestationRepository.findAttestationsExpirees(LocalDateTime.now());

        for (Attestation attestation : attestationsExpirees) {
            attestation.setStatut(StatutAttestation.EXPIREE);
            attestationRepository.save(attestation);
        }
    }

    @Override
    public String genererNumeroAttestation() {
        String prefix = "ATT";
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String numeroSequentiel;

        do {
            numeroSequentiel = String.format("%04d", new Random().nextInt(10000));
        } while (attestationRepository.existsByNumeroAttestation(prefix + date + numeroSequentiel));

        return prefix + date + numeroSequentiel;
    }

    @Override
    public String genererCodeVerification() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }

        return code.toString();
    }

    // Méthode utilitaire de mapping
    private AttestationDto mapToDto(Attestation attestation) {
        String validateurNom = null;
        if (attestation.getValidateurId() != null) {
            try {
                ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(attestation.getValidateurId());
                if (userResponse.getBody() != null) {
                    UserDto user = userResponse.getBody();
                    validateurNom = user.getNom() + " " + user.getPrenom();
                }
            } catch (Exception e) {
                validateurNom = "Validateur inconnu";
            }
        }

        return AttestationDto.builder()
                .id(attestation.getId())
                .numeroAttestation(attestation.getNumeroAttestation())
                .certificationId(attestation.getCertification().getId())
                .certificationNom(attestation.getCertification().getNom())
                .beneficiaireId(attestation.getBeneficiaireId())
                .beneficiaireNom(attestation.getBeneficiaireNom())
                .beneficiaireEmail(attestation.getBeneficiaireEmail())
                .statut(attestation.getStatut())
                .dateObtention(attestation.getDateObtention())
                .dateExpiration(attestation.getDateExpiration())
                .noteObtenue(attestation.getNoteObtenue())
                .noteMaximale(attestation.getNoteMaximale())
                .pourcentageReussite(attestation.getPourcentageReussite())
                .validateurId(attestation.getValidateurId())
                .validateurNom(validateurNom)
                .commentaires(attestation.getCommentaires())
                .cheminFichierPdf(attestation.getCheminFichierPdf())
                .codeVerification(attestation.getCodeVerification())
                .telecharge(attestation.isTelecharge())
                .dateCreation(attestation.getDateCreation())
                .dateMiseAJour(attestation.getDateMiseAJour())
                .valide(attestation.isValide())
                .expiree(attestation.isExpiree())
                .joursRestantsAvantExpiration(attestation.getJoursRestantsAvantExpiration())
                .build();
    }
}