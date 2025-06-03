package net.mooh.certificationservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.certificationservice.client.ContentClient;
import net.mooh.certificationservice.client.EvaluationClient;
import net.mooh.certificationservice.client.UserClient;
import net.mooh.certificationservice.client.UserDto;
import net.mooh.certificationservice.dtos.CertificationDto;
import net.mooh.certificationservice.entities.Certification;
import net.mooh.certificationservice.entities.Certification.TypeCertification;
import net.mooh.certificationservice.exception.DuplicateResourceException;
import net.mooh.certificationservice.exception.ResourceNotFoundException;
import net.mooh.certificationservice.exception.UnauthorizedException;
import net.mooh.certificationservice.repository.AttestationRepository;
import net.mooh.certificationservice.repository.CertificationRepository;
import net.mooh.certificationservice.service.CertificationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final AttestationRepository attestationRepository;
    @Qualifier("net.mooh.certificationservice.client.UserClient")
    private final UserClient userClient;
    @Qualifier("net.mooh.certificationservice.client.ContentClient")
    private final ContentClient contentClient;
    @Qualifier("net.mooh.certificationservice.client.EvaluationClient")
    private final EvaluationClient evaluationClient;

    @Override
    @Transactional
    public CertificationDto creerCertification(CertificationDto certificationDto) {
        // Vérifier que l'utilisateur existe et a le rôle approprié
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(certificationDto.getCreateurId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", certificationDto.getCreateurId());
        }

        UserDto user = userResponse.getBody();
        if (!user.getRoles().contains("FORMATEUR") && !user.getRoles().contains("ADMINISTRATEUR")&& !user.getRoles().contains("GESTIONNAIRE")) {
            throw new UnauthorizedException("L'utilisateur n'a pas le rôle nécessaire pour créer une certification");
        }

        // Vérifier l'unicité du nom
        if (certificationRepository.existsByNom(certificationDto.getNom())) {
            throw new DuplicateResourceException("Une certification avec ce nom existe déjà");
        }

        // Vérifier que la formation existe si spécifiée
        if (certificationDto.getFormationId() != null) {
            try {
                contentClient.getFormationById(certificationDto.getFormationId());
            } catch (Exception e) {
                throw new ResourceNotFoundException("Formation", "id", certificationDto.getFormationId());
            }
        }

        Certification certification = mapToEntity(certificationDto);
        certification.setActive(true);
        Certification savedCertification = certificationRepository.save(certification);
        return mapToDto(savedCertification);
    }

    @Override
    public CertificationDto getCertificationById(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));
        return mapToDto(certification);
    }

    @Override
    public CertificationDto getCertificationDetailById(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        CertificationDto certificationDto = mapToDto(certification);

        // Ajouter les statistiques
        certificationDto.setNbAttestationsDelivrees(
                certificationRepository.countAttestationsByCertificationId(id));
        certificationDto.setNbAttestationsActives(
                certificationRepository.countAttestationsActivesByCertificationId(id));

        return certificationDto;
    }

    @Override
    public Page<CertificationDto> getAllCertifications(Pageable pageable) {
        return certificationRepository.findByActiveTrueOrderByDateCreationDesc(pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<CertificationDto> rechercherCertifications(String searchTerm, Pageable pageable) {
        return certificationRepository.rechercherCertifications(searchTerm, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<CertificationDto> getCertificationsByCreateurId(Long createurId) {
        return certificationRepository.findByCreateurIdAndActiveTrue(createurId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificationDto> getCertificationsByFormationId(Long formationId) {
        return certificationRepository.findByFormationIdAndActiveTrue(formationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CertificationDto> getCertificationsByType(TypeCertification type) {
        return certificationRepository.findByTypeAndActiveTrue(type).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CertificationDto updateCertification(Long id, CertificationDto certificationDto) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));

        // Vérifier l'unicité du nom si modifié
        if (!certification.getNom().equals(certificationDto.getNom()) &&
                certificationRepository.existsByNom(certificationDto.getNom())) {
            throw new DuplicateResourceException("Une certification avec ce nom existe déjà");
        }

        certification.setNom(certificationDto.getNom());
        certification.setDescription(certificationDto.getDescription());
        certification.setType(certificationDto.getType());
        certification.setNoteMinimaleRequise(certificationDto.getNoteMinimaleRequise());
        certification.setDureeValidite(certificationDto.getDureeValidite());
        certification.setCriteresValidation(certificationDto.getCriteresValidation());

        // Mettre à jour les collections si fournies
        if (certificationDto.getFormationsPrerequisites() != null) {
            certification.setFormationsPrerequisites(certificationDto.getFormationsPrerequisites());
        }
        if (certificationDto.getQuizRequis() != null) {
            certification.setQuizRequis(certificationDto.getQuizRequis());
        }
        if (certificationDto.getCompetences() != null) {
            certification.setCompetences(certificationDto.getCompetences());
        }

        Certification updatedCertification = certificationRepository.save(certification);
        return mapToDto(updatedCertification);
    }

    @Override
    @Transactional
    public void deleteCertification(Long id) {
        if (!certificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Certification", "id", id);
        }
        // TODO: Vérifier s'il y a des attestations actives
        certificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activerCertification(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));
        certification.setActive(true);
        certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public void desactiverCertification(Long id) {
        Certification certification = certificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", id));
        certification.setActive(false);
        certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public void ajouterFormationPrerequise(Long certificationId, Long formationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        // Vérifier que la formation existe
        try {
            contentClient.getFormationById(formationId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Formation", "id", formationId);
        }

        certification.ajouterFormationPrerequise(formationId);
        certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public void supprimerFormationPrerequise(Long certificationId, Long formationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        certification.getFormationsPrerequisites().remove(formationId);
        certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public void ajouterQuizRequis(Long certificationId, Long quizId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        // Vérifier que le quiz existe
        try {
            evaluationClient.getQuizById(quizId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Quiz", "id", quizId);
        }

        certification.ajouterQuizRequis(quizId);
        certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public void supprimerQuizRequis(Long certificationId, Long quizId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        certification.getQuizRequis().remove(quizId);
        certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public void ajouterCompetence(Long certificationId, String competence) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        certification.ajouterCompetence(competence);
        certificationRepository.save(certification);
    }

    @Override
    @Transactional
    public void supprimerCompetence(Long certificationId, String competence) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        certification.getCompetences().remove(competence);
        certificationRepository.save(certification);
    }

    @Override
    public boolean peutObtenirCertification(Long beneficiaireId, Long certificationId) {
        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification", "id", certificationId));

        // Vérifier que l'utilisateur n'a pas déjà une attestation active
        if (attestationRepository.findByBeneficiaireIdAndCertificationId(beneficiaireId, certificationId).isPresent()) {
            return false;
        }

        // TODO: Vérifier les prérequis (formations complétées, quiz réussis, etc.)
        // Cette logique devrait appeler les autres microservices pour vérifier
        // - Les formations prérequises sont complétées
        // - Les quiz requis sont réussis avec la note minimale
        // - Les critères de validation sont respectés

        return true;
    }

    // Méthodes utilitaires de mapping
    private CertificationDto mapToDto(Certification certification) {
        String createurNom = null;
        try {
            ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(certification.getCreateurId());
            if (userResponse.getBody() != null) {
                UserDto user = userResponse.getBody();
                createurNom = user.getNom() + " " + user.getPrenom();
            }
        } catch (Exception e) {
            createurNom = "Utilisateur inconnu";
        }

        return CertificationDto.builder()
                .id(certification.getId())
                .nom(certification.getNom())
                .description(certification.getDescription())
                .formationId(certification.getFormationId())
                .createurId(certification.getCreateurId())
                .createurNom(createurNom)
                .type(certification.getType())
                .noteMinimaleRequise(certification.getNoteMinimaleRequise())
                .dureeValidite(certification.getDureeValidite())
                .active(certification.isActive())
                .formationsPrerequisites(certification.getFormationsPrerequisites())
                .quizRequis(certification.getQuizRequis())
                .competences(certification.getCompetences())
                .criteresValidation(certification.getCriteresValidation())
                .dateCreation(certification.getDateCreation())
                .dateMiseAJour(certification.getDateMiseAJour())
                .build();
    }

    private Certification mapToEntity(CertificationDto certificationDto) {
        return Certification.builder()
                .nom(certificationDto.getNom())
                .description(certificationDto.getDescription())
                .formationId(certificationDto.getFormationId())
                .createurId(certificationDto.getCreateurId())
                .type(certificationDto.getType())
                .noteMinimaleRequise(certificationDto.getNoteMinimaleRequise())
                .dureeValidite(certificationDto.getDureeValidite())
                .formationsPrerequisites(certificationDto.getFormationsPrerequisites())
                .quizRequis(certificationDto.getQuizRequis())
                .competences(certificationDto.getCompetences())
                .criteresValidation(certificationDto.getCriteresValidation())
                .build();
    }
}