package net.mooh.contentservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.dtos.SectionFormationDto;
import net.mooh.contentservice.entities.Formation;
import net.mooh.contentservice.entities.SectionFormation;
import net.mooh.contentservice.exception.ResourceNotFoundException;
import net.mooh.contentservice.repository.ContenuRepository;
import net.mooh.contentservice.repository.FormationRepository;
import net.mooh.contentservice.repository.SectionFormationRepository;
import net.mooh.contentservice.service.SectionFormationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionFormationServiceImpl implements SectionFormationService {
    private final SectionFormationRepository sectionRepository;
    private final FormationRepository formationRepository;
    private final ContenuRepository contenuRepository;

    @Override
    @Transactional
    public SectionFormationDto creerSection(Long formationId, SectionFormationDto sectionDto) {
        Formation formation = formationRepository.findById(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Formation", "id", formationId));

        // Déterminer l'ordre de la nouvelle section
        Integer ordre = sectionRepository.findMaxOrdreByFormationId(formationId);
        if (ordre == null) {
            ordre = 0;
        } else {
            ordre += 1;
        }

        SectionFormation section = mapToEntity(sectionDto);
        section.setFormation(formation);
        section.setOrdre(ordre);

        SectionFormation savedSection = sectionRepository.save(section);
        return mapToDto(savedSection);
    }

    @Override
    public SectionFormationDto getSectionById(Long id) {
        SectionFormation section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", id));

        return mapToDto(section);
    }

    @Override
    public List<SectionFormationDto> getSectionsByFormationId(Long formationId) {
        return sectionRepository.findByFormationIdOrderByOrdre(formationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SectionFormationDto updateSection(Long id, SectionFormationDto sectionDto) {
        SectionFormation section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", id));

        section.setTitre(sectionDto.getTitre());
        section.setDescription(sectionDto.getDescription());
        section.setDureeEstimee(sectionDto.getDureeEstimee());

        SectionFormation updatedSection = sectionRepository.save(section);
        return mapToDto(updatedSection);
    }

    @Override
    @Transactional
    public void deleteSection(Long id) {
        if (!sectionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Section", "id", id);
        }

        sectionRepository.deleteById(id);

        // Note: En réalité, il faudrait réorganiser les ordres des sections restantes
    }

    @Override
    @Transactional
    public void deplacerSection(Long id, Integer nouvelOrdre) {
        SectionFormation section = sectionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Section", "id", id));

        Integer ordreActuel = section.getOrdre();
        Long formationId = section.getFormation().getId();

        // Récupérer toutes les sections de la formation
        List<SectionFormation> sections = sectionRepository.findByFormationIdOrderByOrdre(formationId);

        // Vérifier que le nouvel ordre est valide
        if (nouvelOrdre < 0 || nouvelOrdre >= sections.size()) {
            throw new IllegalArgumentException("Ordre invalide");
        }

        // Déplacer la section
        if (ordreActuel < nouvelOrdre) {
            // Déplacer vers le bas
            for (SectionFormation s : sections) {
                if (s.getOrdre() > ordreActuel && s.getOrdre() <= nouvelOrdre) {
                    s.setOrdre(s.getOrdre() - 1);
                    sectionRepository.save(s);
                }
            }
        } else if (ordreActuel > nouvelOrdre) {
            // Déplacer vers le haut
            for (SectionFormation s : sections) {
                if (s.getOrdre() >= nouvelOrdre && s.getOrdre() < ordreActuel) {
                    s.setOrdre(s.getOrdre() + 1);
                    sectionRepository.save(s);
                }
            }
        }

        // Mettre à jour l'ordre de la section déplacée
        section.setOrdre(nouvelOrdre);
        sectionRepository.save(section);
    }

    @Override
    public Integer getNbContenus(Long sectionId) {
        return contenuRepository.countBySectionId(sectionId);
    }

    @Override
    public Integer getDureeEstimee(Long sectionId) {
        return contenuRepository.calculateTotalDurationBySectionId(sectionId);
    }

    // Méthode utilitaire pour mapper une SectionFormation en SectionFormationDto
    private SectionFormationDto mapToDto(SectionFormation section) {
        Integer nbContenus = contenuRepository.countBySectionId(section.getId());

        return SectionFormationDto.builder()
                .id(section.getId())
                .titre(section.getTitre())
                .description(section.getDescription())
                .ordre(section.getOrdre())
                .formationId(section.getFormation().getId())
                .dureeEstimee(section.getDureeEstimee())
                .dateCreation(section.getDateCreation())
                .dateMiseAJour(section.getDateMiseAJour())
                .nbContenus(nbContenus)
                .build();
    }

    // Méthode utilitaire pour mapper un SectionFormationDto en SectionFormation
    private SectionFormation mapToEntity(SectionFormationDto sectionDto) {
        return SectionFormation.builder()
                .titre(sectionDto.getTitre())
                .description(sectionDto.getDescription())
                .dureeEstimee(sectionDto.getDureeEstimee())
                .build();
    }
}
