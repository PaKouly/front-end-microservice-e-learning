package net.mooh.contentservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.client.UserClient;
import net.mooh.contentservice.client.UserDto;
import net.mooh.contentservice.dtos.ProgressionApprenantDto;
import net.mooh.contentservice.entities.Formation;
import net.mooh.contentservice.entities.ProgressionApprenant;
import net.mooh.contentservice.exception.ResourceNotFoundException;
import net.mooh.contentservice.exception.UnauthorizedException;
import net.mooh.contentservice.repository.ContenuRepository;
import net.mooh.contentservice.repository.FormationRepository;
import net.mooh.contentservice.repository.ProgressionApprenantRepository;
import net.mooh.contentservice.repository.SectionFormationRepository;
import net.mooh.contentservice.service.ProgressionApprenantService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressionApprenantServiceImpl implements ProgressionApprenantService {

    private final ProgressionApprenantRepository progressionRepository;
    private final FormationRepository formationRepository;
    private final SectionFormationRepository sectionRepository;
    private final ContenuRepository contenuRepository;
    @Qualifier("net.mooh.contentservice.client.UserClient")
    private final UserClient userClient;  // Client Feign pour communiquer avec User & Auth Service

    @Override
    @Transactional
    public ProgressionApprenantDto inscrireApprenant(Long apprenantId, Long formationId) {
        // 1. Vérifier si l'apprenant est déjà inscrit
        if (progressionRepository.findByApprenantIdAndFormationId(apprenantId, formationId).isPresent()) {
            throw new IllegalArgumentException("L'apprenant est déjà inscrit à cette formation");
        }

        // 2. Vérifier que l'utilisateur existe et a le rôle APPRENANT
        ResponseEntity<UserDto> userResponse;
        try {
            userResponse = userClient.getUtilisateurById(apprenantId);
        } catch (Exception e) {
            throw new ResourceNotFoundException("Utilisateur", "id", apprenantId);
        }

        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", apprenantId);
        }

        UserDto user = userResponse.getBody();

        if (!user.getRoles().contains("APPRENANT")) {
            throw new UnauthorizedException("L'utilisateur n'a pas le rôle APPRENANT nécessaire pour s'inscrire à une formation");
        }

        // 3. Vérifier que la formation existe et est active
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));

        if (!formation.isActive()) {
            throw new IllegalArgumentException("La formation n'est pas active");
        }

        // 4. Créer la progression
        ProgressionApprenant progression = ProgressionApprenant.builder()
                .apprenantId(apprenantId)
                .formation(formation)
                .pourcentageCompletion(0)
                .derniereConsultation(LocalDateTime.now())
                .formationCompletee(false)
                .build();

        ProgressionApprenant savedProgression = progressionRepository.save(progression);
        return mapToDto(savedProgression);
    }
    @Override
    public ProgressionApprenantDto getProgressionByApprenantIdAndFormationId(Long apprenantId, Long formationId) {
        ProgressionApprenant progression = progressionRepository.findByApprenantIdAndFormationId(apprenantId, formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Progression", "apprenantId et formationId", apprenantId + " et " + formationId));

        return mapToDto(progression);
    }

    @Override
    public List<ProgressionApprenantDto> getProgressionsByApprenantId(Long apprenantId) {
        return progressionRepository.findByApprenantId(apprenantId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProgressionApprenantDto> getProgressionsByFormationId(Long formationId) {
        return progressionRepository.findByFormationId(formationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProgressionApprenantDto marquerContenuComplet(Long apprenantId, Long formationId, Long contenuId) {
        ProgressionApprenant progression = progressionRepository.findByApprenantIdAndFormationId(apprenantId, formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Progression", "apprenantId et formationId", apprenantId + " et " + formationId));

        progression.ajouterContenuComplete(contenuId);

        // Recalculer la progression
        return calculerProgression(apprenantId, formationId);
    }

    @Override
    @Transactional
    public ProgressionApprenantDto marquerSectionCompletee(Long apprenantId, Long formationId, Long sectionId) {
        ProgressionApprenant progression = progressionRepository.findByApprenantIdAndFormationId(apprenantId, formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Progression", "apprenantId et formationId", apprenantId + " et " + formationId));

        progression.ajouterSectionCompletee(sectionId);

        // Recalculer la progression
        return calculerProgression(apprenantId, formationId);
    }

    @Override
    @Transactional
    public ProgressionApprenantDto marquerDerniereConsultation(Long apprenantId, Long formationId) {
        ProgressionApprenant progression = progressionRepository.findByApprenantIdAndFormationId(apprenantId, formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Progression", "apprenantId et formationId", apprenantId + " et " + formationId));

        progression.setDerniereConsultation(LocalDateTime.now());

        ProgressionApprenant savedProgression = progressionRepository.save(progression);
        return mapToDto(savedProgression);
    }

    @Override
    @Transactional
    public ProgressionApprenantDto calculerProgression(Long apprenantId, Long formationId) {
        ProgressionApprenant progression = progressionRepository.findByApprenantIdAndFormationId(apprenantId, formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Progression", "apprenantId et formationId", apprenantId + " et " + formationId));

        // Calculer le nombre total de contenus dans la formation
        Integer totalContenus = 0;
        List<Long> sectionIds = sectionRepository.findByFormationIdOrderByOrdre(formationId).stream()
                .map(section -> section.getId())
                .collect(Collectors.toList());

        for (Long sectionId : sectionIds) {
            Integer contenusDansSection = contenuRepository.countBySectionId(sectionId);
            totalContenus += contenusDansSection != null ? contenusDansSection : 0;
        }

        // Calculer le pourcentage de complétion
        if (totalContenus > 0) {
            int completedContenus = progression.getContenusCompletes().size();
            int pourcentage = (int) Math.round((double) completedContenus / totalContenus * 100);
            progression.setPourcentageCompletion(pourcentage);

            // Marquer la formation comme complétée si 100%
            if (pourcentage >= 100) {
                progression.setFormationCompletee(true);
            }
        } else {
            progression.setPourcentageCompletion(0);
        }

        ProgressionApprenant savedProgression = progressionRepository.save(progression);
        return mapToDto(savedProgression);
    }

    @Override
    @Transactional
    public void desinscrireApprenant(Long apprenantId, Long formationId) {
        ProgressionApprenant progression = progressionRepository.findByApprenantIdAndFormationId(apprenantId, formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Progression", "apprenantId et formationId", apprenantId + " et " + formationId));

        progressionRepository.delete(progression);
    }

    @Override
    public Double getTauxCompletionFormation(Long formationId) {
        return progressionRepository.getAverageCompletionByFormationId(formationId);
    }

    @Override
    public Integer getNbInscritsFormation(Long formationId) {
        return progressionRepository.countByFormationId(formationId);
    }

    // Méthode utilitaire pour mapper un ProgressionApprenant en ProgressionApprenantDto
    private ProgressionApprenantDto mapToDto(ProgressionApprenant progression) {
        return ProgressionApprenantDto.builder()
                .id(progression.getId())
                .apprenantId(progression.getApprenantId())
                .formationId(progression.getFormation().getId())
                .formationTitre(progression.getFormation().getTitre())
                .contenusCompletes(progression.getContenusCompletes())
                .sectionsCompletees(progression.getSectionsCompletees())
                .pourcentageCompletion(progression.getPourcentageCompletion())
                .derniereConsultation(progression.getDerniereConsultation())
                .dateInscription(progression.getDateInscription())
                .dateMiseAJour(progression.getDateMiseAJour())
                .formationCompletee(progression.isFormationCompletee())
                .build();
    }
}
