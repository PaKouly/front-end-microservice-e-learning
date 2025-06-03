package net.mooh.forumservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.client.UserClient;
import net.mooh.forumservice.client.UserDto;
import net.mooh.forumservice.dtos.MessageDto;
import net.mooh.forumservice.entities.Message;
import net.mooh.forumservice.entities.Sujet;
import net.mooh.forumservice.exception.ResourceNotFoundException;
import net.mooh.forumservice.exception.UnauthorizedException;
import net.mooh.forumservice.repository.MessageRepository;
import net.mooh.forumservice.repository.SujetRepository;
import net.mooh.forumservice.service.MessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SujetRepository sujetRepository;
    @Qualifier("net.mooh.forumservice.client.UserClient")
    private final UserClient userClient;

    @Override
    @Transactional
    public MessageDto creerMessage(MessageDto messageDto) {
        // Vérifier que l'utilisateur existe
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(messageDto.getAuteurId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", messageDto.getAuteurId());
        }

        UserDto user = userResponse.getBody();

        // Vérifier que le sujet existe et n'est pas verrouillé
        Sujet sujet = sujetRepository.findById(messageDto.getSujetId())
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", messageDto.getSujetId()));

        if (sujet.isVerrouille()) {
            throw new UnauthorizedException("Ce sujet est verrouillé, impossible d'ajouter des messages");
        }

        // Créer le message avec initialisation explicite des collections
        Message message = Message.builder()
                .contenu(messageDto.getContenu())
                .auteurId(messageDto.getAuteurId())
                .auteurNom(user.getNom() + " " + user.getPrenom())
                .sujet(sujet)
                .estValide(true)
                .estModifie(false)
                .likes(new HashSet<>()) // Initialisation explicite
                .reponses(new HashSet<>()) // Initialisation explicite
                .build();

        Message savedMessage = messageRepository.save(message);
        return mapToDto(savedMessage, null);
    }

    @Override
    @Transactional
    public MessageDto repondreMessage(Long messageParentId, MessageDto messageDto) {
        // Vérifier que le message parent existe
        Message messageParent = messageRepository.findById(messageParentId)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", messageParentId));

        // Vérifier que l'utilisateur existe
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(messageDto.getAuteurId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", messageDto.getAuteurId());
        }

        UserDto user = userResponse.getBody();

        // Vérifier que le sujet n'est pas verrouillé
        if (messageParent.getSujet().isVerrouille()) {
            throw new UnauthorizedException("Ce sujet est verrouillé, impossible d'ajouter des réponses");
        }

        // Créer la réponse avec initialisation explicite des collections
        Message reponse = Message.builder()
                .contenu(messageDto.getContenu())
                .auteurId(messageDto.getAuteurId())
                .auteurNom(user.getNom() + " " + user.getPrenom())
                .sujet(messageParent.getSujet())
                .messageParent(messageParent)
                .estValide(true)
                .estModifie(false)
                .likes(new HashSet<>()) // Initialisation explicite
                .reponses(new HashSet<>()) // Initialisation explicite
                .build();

        Message savedReponse = messageRepository.save(reponse);
        return mapToDto(savedReponse, null);
    }

    @Override
    public MessageDto getMessageById(Long id, Long utilisateurId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

        return mapToDto(message, utilisateurId);
    }

    @Override
    public Page<MessageDto> getMessagesBySujetId(Long sujetId, Pageable pageable, Long utilisateurId) {
        return messageRepository.findBySujetIdAndMessageParentIsNullOrderByDateCreationAsc(sujetId, pageable)
                .map(message -> mapToDtoWithReplies(message, utilisateurId));
    }

    @Override
    public List<MessageDto> getMessagesByAuteurId(Long auteurId) {
        return messageRepository.findByAuteurId(auteurId).stream()
                .map(message -> mapToDto(message, null))
                .collect(Collectors.toList());
    }

    @Override
    public Page<MessageDto> getMessagesNonValides(Long sujetId, Pageable pageable) {
        return messageRepository.findNonValidatedMessagesBySujetId(sujetId, pageable)
                .map(message -> mapToDto(message, null));
    }

    @Override
    @Transactional
    public MessageDto updateMessage(Long id, MessageDto messageDto, Long utilisateurId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

        // Vérifier que l'utilisateur est l'auteur du message
        if (!message.getAuteurId().equals(utilisateurId)) {
            throw new UnauthorizedException("Seul l'auteur peut modifier ce message");
        }

        // Vérifier que le sujet n'est pas verrouillé
        if (message.getSujet().isVerrouille()) {
            throw new UnauthorizedException("Ce sujet est verrouillé, impossible de modifier le message");
        }

        message.setContenu(messageDto.getContenu());
        message.setEstModifie(true);

        Message updatedMessage = messageRepository.save(message);
        return mapToDto(updatedMessage, utilisateurId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id, Long utilisateurId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

        // Vérifier que l'utilisateur est l'auteur ou a les droits de modération
        if (!message.getAuteurId().equals(utilisateurId) && !estModerateur(utilisateurId)) {
            throw new UnauthorizedException("Vous n'avez pas le droit de supprimer ce message");
        }

        messageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void validerMessage(Long id, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent valider des messages");
        }

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

        message.setEstValide(true);
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void like(Long id, Long utilisateurId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

        // Ajouter le like en s'assurant que la collection est initialisée
        message.getLikes().add(utilisateurId);
        messageRepository.save(message);
    }

    @Override
    @Transactional
    public void unlike(Long id, Long utilisateurId) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));

        // Supprimer le like en s'assurant que la collection est initialisée
        message.getLikes().remove(utilisateurId);
        messageRepository.save(message);
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

    // Méthode utilitaire pour mapper un Message en MessageDto (avec protection null)
    private MessageDto mapToDto(Message message, Long utilisateurId) {
        boolean likeParUtilisateur = false;
        if (utilisateurId != null && message.getLikes() != null) {
            likeParUtilisateur = message.getLikes().contains(utilisateurId);
        }

        int nbLikes = message.getLikes() != null ? message.getLikes().size() : 0;

        return MessageDto.builder()
                .id(message.getId())
                .contenu(message.getContenu())
                .auteurId(message.getAuteurId())
                .auteurNom(message.getAuteurNom())
                .sujetId(message.getSujet().getId())
                .messageParentId(message.getMessageParent() != null ? message.getMessageParent().getId() : null)
                .estValide(message.isEstValide())
                .estModifie(message.isEstModifie())
                .nbLikes(nbLikes)
                .likeParUtilisateur(likeParUtilisateur)
                .dateCreation(message.getDateCreation())
                .dateMiseAJour(message.getDateMiseAJour())
                .build();
    }

    // Méthode utilitaire pour mapper un Message avec ses réponses
    private MessageDto mapToDtoWithReplies(Message message, Long utilisateurId) {
        MessageDto messageDto = mapToDto(message, utilisateurId);

        // Ajouter les réponses
        List<MessageDto> reponses = messageRepository.findByMessageParentIdOrderByDateCreationAsc(message.getId())
                .stream()
                .map(reponse -> mapToDto(reponse, utilisateurId))
                .collect(Collectors.toList());

        messageDto.setReponses(reponses);
        return messageDto;
    }
}