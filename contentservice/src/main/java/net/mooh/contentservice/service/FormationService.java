package net.mooh.contentservice.service;

import net.mooh.contentservice.dtos.FormationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FormationService {
    FormationDto creerFormation(FormationDto formationDto);

    FormationDto getFormationById(Long id);

    FormationDto getFormationDetailById(Long id);

    Page<FormationDto> getAllFormations(Pageable pageable);

    Page<FormationDto> rechercherFormations(String searchTerm, Pageable pageable);

    List<FormationDto> getFormationsByCreateurId(Long createurId);

    List<FormationDto> getFormationsByTag(String tag);

    List<FormationDto> getFormationsByNiveau(String niveau);

    List<FormationDto> getFormationsByLangue(String langue);

    FormationDto updateFormation(Long id, FormationDto formationDto);

    void deleteFormation(Long id);

    void activerFormation(Long id);

    void desactiverFormation(Long id);

    void ajouterTag(Long formationId, String tag);

    void supprimerTag(Long formationId, String tag);

    void ajouterPrerequis(Long formationId, String prerequis);

    void supprimerPrerequis(Long formationId, String prerequis);

    void ajouterObjectif(Long formationId, String objectif);

    void supprimerObjectif(Long formationId, String objectif);

    List<String> getAllTags();

    List<String> getAllNiveaux();

    List<String> getAllLangues();
}
