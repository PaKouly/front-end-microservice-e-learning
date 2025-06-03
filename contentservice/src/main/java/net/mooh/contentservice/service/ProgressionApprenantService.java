package net.mooh.contentservice.service;

import net.mooh.contentservice.dtos.ProgressionApprenantDto;

import java.util.List;

public interface ProgressionApprenantService {

    ProgressionApprenantDto inscrireApprenant(Long apprenantId, Long formationId);

    ProgressionApprenantDto getProgressionByApprenantIdAndFormationId(Long apprenantId, Long formationId);

    List<ProgressionApprenantDto> getProgressionsByApprenantId(Long apprenantId);

    List<ProgressionApprenantDto> getProgressionsByFormationId(Long formationId);

    ProgressionApprenantDto marquerContenuComplet(Long apprenantId, Long formationId, Long contenuId);

    ProgressionApprenantDto marquerSectionCompletee(Long apprenantId, Long formationId, Long sectionId);

    ProgressionApprenantDto marquerDerniereConsultation(Long apprenantId, Long formationId);

    ProgressionApprenantDto calculerProgression(Long apprenantId, Long formationId);

    void desinscrireApprenant(Long apprenantId, Long formationId);

    Double getTauxCompletionFormation(Long formationId);

    Integer getNbInscritsFormation(Long formationId);
}
