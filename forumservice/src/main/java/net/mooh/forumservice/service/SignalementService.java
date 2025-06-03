package net.mooh.forumservice.service;

import net.mooh.forumservice.dtos.SignalementDto;
import net.mooh.forumservice.entities.Signalement.StatutSignalement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SignalementService {

    SignalementDto creerSignalement(SignalementDto signalementDto);

    SignalementDto getSignalementById(Long id);

    Page<SignalementDto> getSignalementsByStatut(StatutSignalement statut, Pageable pageable);

    Page<SignalementDto> getSignalementsByForumIdAndStatut(Long forumId, StatutSignalement statut, Pageable pageable);

    List<SignalementDto> getSignalementsByMessageId(Long messageId);

    SignalementDto updateSignalement(Long id, SignalementDto signalementDto, Long moderateurId);

    void marquerEnCours(Long id, Long moderateurId);

    void marquerResolu(Long id, String commentaire, Long moderateurId);

    void marquerRejete(Long id, String commentaire, Long moderateurId);

    Integer countSignalementsNonResolus(Long forumId);
}
