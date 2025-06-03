package net.mooh.forumservice.service;

import net.mooh.forumservice.dtos.SujetDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SujetService {

    SujetDto creerSujet(SujetDto sujetDto);

    SujetDto getSujetById(Long id, Long utilisateurId);

    Page<SujetDto> getSujetsByForumId(Long forumId, Pageable pageable);

    Page<SujetDto> rechercherSujets(Long forumId, String searchTerm, Pageable pageable);

    List<SujetDto> getSujetsByAuteurId(Long auteurId);

    Page<SujetDto> getSujetsResolus(Long forumId, Pageable pageable);

    SujetDto updateSujet(Long id, SujetDto sujetDto, Long utilisateurId);

    void deleteSujet(Long id, Long utilisateurId);

    void marquerResolu(Long id, Long utilisateurId);

    void epingler(Long id, Long moderateurId);

    void desepingler(Long id, Long moderateurId);

    void verrouiller(Long id, Long moderateurId);

    void deverrouiller(Long id, Long moderateurId);
}
