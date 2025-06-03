package net.mooh.contentservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.dtos.ContenuDto;
import net.mooh.contentservice.entities.Contenu;
import net.mooh.contentservice.entities.SectionFormation;
import net.mooh.contentservice.exception.ResourceNotFoundException;
import net.mooh.contentservice.repository.ContenuRepository;
import net.mooh.contentservice.repository.SectionFormationRepository;
import net.mooh.contentservice.service.ContenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContenuServiceImpl implements ContenuService {

    private final ContenuRepository contenuRepository;
    private final SectionFormationRepository sectionRepository;

    @Override
    @Transactional
    public ContenuDto creerContenu(Long sectionId, ContenuDto contenuDto) {
        SectionFormation section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", sectionId));

        // Déterminer l'ordre du nouveau contenu
        Integer ordre = contenuRepository.findMaxOrdreBySectionId(sectionId);
        if (ordre == null) {
            ordre = 0;
        } else {
            ordre += 1;
        }

        Contenu contenu = mapToEntity(contenuDto);
        contenu.setSection(section);
        contenu.setOrdre(ordre);

        Contenu savedContenu = contenuRepository.save(contenu);

        // Mettre à jour la durée estimée de la section
        updateSectionDuration(section);

        return mapToDto(savedContenu);
    }

    @Override
    public ContenuDto getContenuById(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu", "id", id));

        return mapToDto(contenu);
    }

    @Override
    public List<ContenuDto> getContenusBySectionId(Long sectionId) {
        return contenuRepository.findBySectionIdOrderByOrdre(sectionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ContenuDto> getContenusByType(Contenu.TypeContenu type, Long formationId) {
        return contenuRepository.findByTypeAndSectionFormationId(type, formationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ContenuDto updateContenu(Long id, ContenuDto contenuDto) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu", "id", id));

        contenu.setTitre(contenuDto.getTitre());
        contenu.setType(contenuDto.getType());
        contenu.setUrl(contenuDto.getUrl());
        contenu.setTexte(contenuDto.getTexte());
        contenu.setLangue(contenuDto.getLangue());
        contenu.setFormat(contenuDto.getFormat());
        contenu.setDureeEstimee(contenuDto.getDureeEstimee());

        Contenu updatedContenu = contenuRepository.save(contenu);

        // Mettre à jour la durée estimée de la section
        updateSectionDuration(contenu.getSection());

        return mapToDto(updatedContenu);
    }

    @Override
    @Transactional
    public void deleteContenu(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu", "id", id));

        SectionFormation section = contenu.getSection();

        contenuRepository.deleteById(id);

        // Mettre à jour la durée estimée de la section
        updateSectionDuration(section);

        // Note: En réalité, il faudrait réorganiser les ordres des contenus restants
    }

    @Override
    @Transactional
    public void deplacerContenu(Long id, Integer nouvelOrdre) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu", "id", id));

        Integer ordreActuel = contenu.getOrdre();
        Long sectionId = contenu.getSection().getId();

        // Récupérer tous les contenus de la section
        List<Contenu> contenus = contenuRepository.findBySectionIdOrderByOrdre(sectionId);

        // Vérifier que le nouvel ordre est valide
        if (nouvelOrdre < 0 || nouvelOrdre >= contenus.size()) {
            throw new IllegalArgumentException("Ordre invalide");
        }

        // Déplacer le contenu
        if (ordreActuel < nouvelOrdre) {
            // Déplacer vers le bas
            for (Contenu c : contenus) {
                if (c.getOrdre() > ordreActuel && c.getOrdre() <= nouvelOrdre) {
                    c.setOrdre(c.getOrdre() - 1);
                    contenuRepository.save(c);
                }
            }
        } else if (ordreActuel > nouvelOrdre) {
            // Déplacer vers le haut
            for (Contenu c : contenus) {
                if (c.getOrdre() >= nouvelOrdre && c.getOrdre() < ordreActuel) {
                    c.setOrdre(c.getOrdre() + 1);
                    contenuRepository.save(c);
                }
            }
        }

        // Mettre à jour l'ordre du contenu déplacé
        contenu.setOrdre(nouvelOrdre);
        contenuRepository.save(contenu);
    }

    @Override
    public Integer getDureeEstimee(Long contenuId) {
        Contenu contenu = contenuRepository.findById(contenuId)
                .orElseThrow(() -> new ResourceNotFoundException("Contenu", "id", contenuId));

        return contenu.getDureeEstimee();
    }

    // Méthode utilitaire pour mettre à jour la durée estimée d'une section
    private void updateSectionDuration(SectionFormation section) {
        Integer totalDuration = contenuRepository.calculateTotalDurationBySectionId(section.getId());
        if (totalDuration != null) {
            section.setDureeEstimee(totalDuration);
            sectionRepository.save(section);
        }
    }

    // Méthode utilitaire pour mapper un Contenu en ContenuDto
    private ContenuDto mapToDto(Contenu contenu) {
        return ContenuDto.builder()
                .id(contenu.getId())
                .titre(contenu.getTitre())
                .type(contenu.getType())
                .url(contenu.getUrl())
                .texte(contenu.getTexte())
                .ordre(contenu.getOrdre())
                .langue(contenu.getLangue())
                .format(contenu.getFormat())
                .sectionId(contenu.getSection().getId())
                .dureeEstimee(contenu.getDureeEstimee())
                .dateCreation(contenu.getDateCreation())
                .dateMiseAJour(contenu.getDateMiseAJour())
                .build();
    }

    // Méthode utilitaire pour mapper un ContenuDto en Contenu
    private Contenu mapToEntity(ContenuDto contenuDto) {
        return Contenu.builder()
                .titre(contenuDto.getTitre())
                .type(contenuDto.getType())
                .url(contenuDto.getUrl())
                .texte(contenuDto.getTexte())
                .langue(contenuDto.getLangue())
                .format(contenuDto.getFormat())
                .dureeEstimee(contenuDto.getDureeEstimee())
                .build();
    }
}
