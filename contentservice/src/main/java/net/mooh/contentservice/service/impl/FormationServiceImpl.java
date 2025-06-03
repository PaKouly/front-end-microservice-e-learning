package net.mooh.contentservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.client.UserClient;
import net.mooh.contentservice.client.UserDto;
import net.mooh.contentservice.dtos.FormationDto;
import net.mooh.contentservice.dtos.SectionFormationDto;
import net.mooh.contentservice.entities.Formation;
import net.mooh.contentservice.exception.ResourceNotFoundException;
import net.mooh.contentservice.exception.UnauthorizedException;
import net.mooh.contentservice.repository.FormationRepository;
import net.mooh.contentservice.repository.ProgressionApprenantRepository;
import net.mooh.contentservice.repository.SectionFormationRepository;
import net.mooh.contentservice.service.FormationService;
import net.mooh.contentservice.service.SectionFormationService;
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
public class FormationServiceImpl implements FormationService {

    private final FormationRepository formationRepository;
    private final SectionFormationRepository sectionRepository;
    private final ProgressionApprenantRepository progressionRepository;
    private final SectionFormationService sectionService;
    @Qualifier("net.mooh.contentservice.client.UserClient")
    private final UserClient userClient;

    @Override
    @Transactional
    public FormationDto creerFormation(FormationDto formationDto) {
        // 1. Vérifier que l'utilisateur existe
        ResponseEntity<UserDto> userResponse;
        try {
            userResponse = userClient.getUtilisateurById(formationDto.getCreateurId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Utilisateur", "id", formationDto.getCreateurId());
        }

        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", formationDto.getCreateurId());
        }

        UserDto user = userResponse.getBody();

        // 2. Vérifier que l'utilisateur a le rôle FORMATEUR
        if (!user.getRoles().contains("FORMATEUR")) {
            throw new UnauthorizedException("L'utilisateur n'a pas le rôle FORMATEUR nécessaire pour créer une formation");
        }

        // 3. Créer la formation si les vérifications sont passées
        Formation formation = mapToEntity(formationDto);
        formation.setActive(true);
        Formation savedFormation = formationRepository.save(formation);
        return mapToDto(savedFormation);
    }

    @Override
    public FormationDto getFormationById(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));
        return mapToDto(formation);
    }

    @Override
    public FormationDto getFormationDetailById(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));

        FormationDto formationDto = mapToDto(formation);

        // Ajouter les détails des sections
        List<SectionFormationDto> sections = sectionService.getSectionsByFormationId(id);
        formationDto.setSections(sections);

        // Ajouter des statistiques supplémentaires
        formationDto.setNbSections(sections.size());
        formationDto.setNbInscrits(progressionRepository.countByFormationId(id));
        formationDto.setTauxCompletion(progressionRepository.getAverageCompletionByFormationId(id));

        return formationDto;
    }

    @Override
    public Page<FormationDto> getAllFormations(Pageable pageable) {
        return formationRepository.findByActiveTrueOrderByDateCreationDesc(pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<FormationDto> rechercherFormations(String searchTerm, Pageable pageable) {
        return formationRepository.rechercherFormations(searchTerm, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<FormationDto> getFormationsByCreateurId(Long createurId) {
        return formationRepository.findByCreateurId(createurId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDto> getFormationsByTag(String tag) {
        return formationRepository.findByTag(tag).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDto> getFormationsByNiveau(String niveau) {
        return formationRepository.findByNiveauAndActiveTrue(niveau).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FormationDto> getFormationsByLangue(String langue) {
        return formationRepository.findByLangueAndActiveTrue(langue).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public FormationDto updateFormation(Long id, FormationDto formationDto) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));

        // Mettre à jour les champs de base
        formation.setTitre(formationDto.getTitre());
        formation.setDescription(formationDto.getDescription());
        formation.setNiveau(formationDto.getNiveau());
        formation.setLangue(formationDto.getLangue());
        formation.setPublique(formationDto.isPublique());

        // Mettre à jour les collections si elles sont fournies
        if (formationDto.getPrerequis() != null) {
            formation.setPrerequis(formationDto.getPrerequis());
        }

        if (formationDto.getObjectifs() != null) {
            formation.setObjectifs(formationDto.getObjectifs());
        }

        if (formationDto.getTags() != null) {
            formation.setTags(formationDto.getTags());
        }

        Formation updatedFormation = formationRepository.save(formation);
        return mapToDto(updatedFormation);
    }

    @Override
    @Transactional
    public void deleteFormation(Long id) {
        if (!formationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Formation", "id", id);
        }
        // Note: En réalité, vérifiez d'abord s'il y a des dépendances (apprenants inscrits, etc.)
        formationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activerFormation(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));
        formation.setActive(true);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void desactiverFormation(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", id));
        formation.setActive(false);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void ajouterTag(Long formationId, String tag) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));
        formation.getTags().add(tag);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void supprimerTag(Long formationId, String tag) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));
        formation.getTags().remove(tag);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void ajouterPrerequis(Long formationId, String prerequis) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));
        formation.getPrerequis().add(prerequis);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void supprimerPrerequis(Long formationId, String prerequis) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));
        formation.getPrerequis().remove(prerequis);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void ajouterObjectif(Long formationId, String objectif) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));
        formation.getObjectifs().add(objectif);
        formationRepository.save(formation);
    }

    @Override
    @Transactional
    public void supprimerObjectif(Long formationId, String objectif) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));
        formation.getObjectifs().remove(objectif);
        formationRepository.save(formation);
    }

    @Override
    public List<String> getAllTags() {
        return formationRepository.findAllTags();
    }

    @Override
    public List<String> getAllNiveaux() {
        return formationRepository.findAllNiveaux();
    }

    @Override
    public List<String> getAllLangues() {
        return formationRepository.findAllLangues();
    }

    // Méthode utilitaire pour mapper une Formation en FormationDto
    private FormationDto mapToDto(Formation formation) {
        // Compter le nombre de sections
        Integer nbSections = sectionRepository.countByFormationId(formation.getId());

        // Récupérer le nombre d'inscrits (si nécessaire)
        Integer nbInscrits = null;
        if (formation.isPublique()) {
            nbInscrits = progressionRepository.countByFormationId(formation.getId());
        }

        return FormationDto.builder()
                .id(formation.getId())
                .titre(formation.getTitre())
                .description(formation.getDescription())
                .niveau(formation.getNiveau())
                .duree(formation.getDuree())
                .langue(formation.getLangue())
                .publique(formation.isPublique())
                .active(formation.isActive())
                .prerequis(formation.getPrerequis())
                .objectifs(formation.getObjectifs())
                .tags(formation.getTags())
                .createurId(formation.getCreateurId())
                .dateCreation(formation.getDateCreation())
                .dateMiseAJour(formation.getDateMiseAJour())
                .nbSections(nbSections)
                .nbInscrits(nbInscrits)
                .build();
    }

    // Méthode utilitaire pour mapper un FormationDto en Formation
    private Formation mapToEntity(FormationDto formationDto) {
        return Formation.builder()
                .titre(formationDto.getTitre())
                .description(formationDto.getDescription())
                .niveau(formationDto.getNiveau())
                .duree(formationDto.getDuree())
                .langue(formationDto.getLangue())
                .publique(formationDto.isPublique())
                .active(formationDto.isActive())
                .prerequis(formationDto.getPrerequis())
                .objectifs(formationDto.getObjectifs())
                .tags(formationDto.getTags())
                .createurId(formationDto.getCreateurId())
                .build();
    }
}
