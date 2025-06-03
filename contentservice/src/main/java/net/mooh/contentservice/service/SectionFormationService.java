package net.mooh.contentservice.service;

import net.mooh.contentservice.dtos.SectionFormationDto;

import java.util.List;

public interface SectionFormationService {

    SectionFormationDto creerSection(Long formationId, SectionFormationDto sectionDto);

    SectionFormationDto getSectionById(Long id);

    List<SectionFormationDto> getSectionsByFormationId(Long formationId);

    SectionFormationDto updateSection(Long id, SectionFormationDto sectionDto);

    void deleteSection(Long id);

    void deplacerSection(Long id, Integer nouvelOrdre);

    Integer getNbContenus(Long sectionId);

    Integer getDureeEstimee(Long sectionId);

}
