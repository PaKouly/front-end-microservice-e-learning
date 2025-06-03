package net.mooh.contentservice.service;

import net.mooh.contentservice.dtos.ContenuDto;
import net.mooh.contentservice.entities.Contenu;

import java.util.List;

public interface ContenuService {
    ContenuDto creerContenu(Long sectionId, ContenuDto contenuDto);

    ContenuDto getContenuById(Long id);

    List<ContenuDto> getContenusBySectionId(Long sectionId);

    List<ContenuDto> getContenusByType(Contenu.TypeContenu type, Long formationId);

    ContenuDto updateContenu(Long id, ContenuDto contenuDto);

    void deleteContenu(Long id);

    void deplacerContenu(Long id, Integer nouvelOrdre);

    Integer getDureeEstimee(Long contenuId);
}
