package net.mooh.forumservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.client.UserClient;
import net.mooh.forumservice.client.UserDto;
import net.mooh.forumservice.dtos.MessageDto;
import net.mooh.forumservice.dtos.SignalementDto;
import net.mooh.forumservice.entities.Message;
import net.mooh.forumservice.entities.Signalement;
import net.mooh.forumservice.entities.Signalement.StatutSignalement;
import net.mooh.forumservice.exception.ResourceNotFoundException;
import net.mooh.forumservice.exception.UnauthorizedException;
import net.mooh.forumservice.repository.MessageRepository;
import net.mooh.forumservice.repository.SignalementRepository;
import net.mooh.forumservice.service.SignalementService;
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
public class SignalementServiceImpl implements SignalementService {

    private final SignalementRepository signalementRepository;
    private final MessageRepository messageRepository;
    @Qualifier("net.mooh.forumservice.client.UserClient")
    private final UserClient userClient;

    @Override
    @Transactional
    public SignalementDto creerSignalement(SignalementDto signalementDto) {
        // Vérifier que l'utilisateur existe
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(signalementDto.getSignaleurId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", signalementDto.getSignaleurId());
        }

        UserDto user = userResponse.getBody();

        // Vérifier que le message existe
        Message message = messageRepository.findById(signalementDto.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", signalementDto.getMessageId()));

        // Vérifier que l'utilisateur ne signale pas son propre message
        if (message.getAuteurId().equals(signalementDto.getSignaleurId())) {
            throw new UnauthorizedException("Vous ne pouvez pas signaler votre propre message");
        }

        // Créer le signalement
        Signalement signalement = Signalement.builder()
                .message(message)
                .signaleurId(signalementDto.getSignaleurId())
                .raison(signalementDto.getRaison())
                .description(signalementDto.getDescription())
                .statut(StatutSignalement.NOUVEAU)
                .build();

        Signalement savedSignalement = signalementRepository.save(signalement);
        return mapToDto(savedSignalement);
    }

    @Override
    public SignalementDto getSignalementById(Long id) {
        Signalement signalement = signalementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Signalement", "id", id));

        return mapToDto(signalement);
    }

    @Override
    public Page<SignalementDto> getSignalementsByStatut(StatutSignalement statut, Pageable pageable) {
        return signalementRepository.findByStatutOrderByDateCreationDesc(statut, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<SignalementDto> getSignalementsByForumIdAndStatut(Long forumId, StatutSignalement statut, Pageable pageable) {
        return signalementRepository.findByForumIdAndStatut(forumId, statut, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<SignalementDto> getSignalementsByMessageId(Long messageId) {
        return signalementRepository.findByMessageIdOrderByDateCreationDesc(messageId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SignalementDto updateSignalement(Long id, SignalementDto signalementDto, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent modifier des signalements");
        }

        Signalement signalement = signalementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Signalement", "id", id));

        signalement.setCommentaireModerateurr(signalementDto.getCommentaireModerateurr());
        signalement.setModerateurId(moderateurId);

        Signalement updatedSignalement = signalementRepository.save(signalement);
        return mapToDto(updatedSignalement);
    }

    @Override
    @Transactional
    public void marquerEnCours(Long id, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent traiter des signalements");
        }

        Signalement signalement = signalementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Signalement", "id", id));

        signalement.setStatut(StatutSignalement.EN_COURS);
        signalement.setModerateurId(moderateurId);
        signalementRepository.save(signalement);
    }

    @Override
    @Transactional
    public void marquerResolu(Long id, String commentaire, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent résoudre des signalements");
        }

        Signalement signalement = signalementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Signalement", "id", id));

        signalement.setStatut(StatutSignalement.RESOLU);
        signalement.setCommentaireModerateurr(commentaire);
        signalement.setModerateurId(moderateurId);
        signalementRepository.save(signalement);
    }

    @Override
    @Transactional
    public void marquerRejete(Long id, String commentaire, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent rejeter des signalements");
        }

        Signalement signalement = signalementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Signalement", "id", id));

        signalement.setStatut(StatutSignalement.REJETE);
        signalement.setCommentaireModerateurr(commentaire);
        signalement.setModerateurId(moderateurId);
        signalementRepository.save(signalement);
    }

    @Override
    public Integer countSignalementsNonResolus(Long forumId) {
        return signalementRepository.countByForumIdAndStatut(forumId, StatutSignalement.NOUVEAU) +
                signalementRepository.countByForumIdAndStatut(forumId, StatutSignalement.EN_COURS);
    }

    // Méthode utilitaire pour vérifier si un utilisateur est modérateur
    private boolean estModerateur(Long utilisateurId) {
        try {
            ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(utilisateurId);
            if (userResponse.getBody() != null) {
                UserDto user = userResponse.getBody();
                return user.getRoles().contains("MODERATEUR") || user.getRoles().contains("ADMINISTRATEUR");
            }
        } catch (Exception e) {
            // En cas d'erreur, considérer que l'utilisateur n'est pas modérateur
        }
        return false;
    }

    // Méthode utilitaire pour mapper un Signalement en SignalementDto
    private SignalementDto mapToDto(Signalement signalement) {
        // Récupérer les informations du signaleur
        String signaleurNom = null;
        try {
            ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(signalement.getSignaleurId());
            if (userResponse.getBody() != null) {
                UserDto user = userResponse.getBody();
                signaleurNom = user.getNom() + " " + user.getPrenom();
            }
        } catch (Exception e) {
            signaleurNom = "Utilisateur inconnu";
        }

        // Récupérer les informations du modérateur si disponible
        String moderateurNom = null;
        if (signalement.getModerateurId() != null) {
            try {
                ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(signalement.getModerateurId());
                if (userResponse.getBody() != null) {
                    UserDto user = userResponse.getBody();
                    moderateurNom = user.getNom() + " " + user.getPrenom();
                }
            } catch (Exception e) {
                moderateurNom = "Modérateur inconnu";
            }
        }

        // Mapper le message si nécessaire
        MessageDto messageDto = null;
        if (signalement.getMessage() != null) {
            Message message = signalement.getMessage();
            messageDto = MessageDto.builder()
                    .id(message.getId())
                    .contenu(message.getContenu())
                    .auteurId(message.getAuteurId())
                    .auteurNom(message.getAuteurNom())
                    .sujetId(message.getSujet().getId())
                    .estValide(message.isEstValide())
                    .estModifie(message.isEstModifie())
                    .nbLikes(message.getNbLikes())
                    .dateCreation(message.getDateCreation())
                    .dateMiseAJour(message.getDateMiseAJour())
                    .build();
        }

        return SignalementDto.builder()
                .id(signalement.getId())
                .messageId(signalement.getMessage().getId())
                .message(messageDto)
                .signaleurId(signalement.getSignaleurId())
                .signaleurNom(signaleurNom)
                .raison(signalement.getRaison())
                .description(signalement.getDescription())
                .statut(signalement.getStatut())
                .commentaireModerateurr(signalement.getCommentaireModerateurr())
                .moderateurId(signalement.getModerateurId())
                .moderateurNom(moderateurNom)
                .dateCreation(signalement.getDateCreation())
                .dateMiseAJour(signalement.getDateMiseAJour())
                .build();
    }
}
