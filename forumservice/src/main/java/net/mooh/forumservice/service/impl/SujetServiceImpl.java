package net.mooh.forumservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.client.UserClient;
import net.mooh.forumservice.client.UserDto;
import net.mooh.forumservice.dtos.SujetDto;
import net.mooh.forumservice.entities.Forum;
import net.mooh.forumservice.entities.Sujet;
import net.mooh.forumservice.exception.ResourceNotFoundException;
import net.mooh.forumservice.exception.UnauthorizedException;
import net.mooh.forumservice.repository.ForumRepository;
import net.mooh.forumservice.repository.MessageRepository;
import net.mooh.forumservice.repository.SujetRepository;
import net.mooh.forumservice.service.SujetService;
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
public class SujetServiceImpl implements SujetService {

    private final SujetRepository sujetRepository;
    private final ForumRepository forumRepository;
    private final MessageRepository messageRepository;
    @Qualifier("net.mooh.forumservice.client.UserClient")
    private final UserClient userClient;

    @Override
    @Transactional
    public SujetDto creerSujet(SujetDto sujetDto) {
        // Vérifier que l'utilisateur existe
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(sujetDto.getAuteurId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", sujetDto.getAuteurId());
        }

        UserDto user = userResponse.getBody();

        // Vérifier que le forum existe
        Forum forum = forumRepository.findById(sujetDto.getForumId())
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", sujetDto.getForumId()));

        // Créer le sujet
        Sujet sujet = Sujet.builder()
                .titre(sujetDto.getTitre())
                .contenu(sujetDto.getContenu())
                .auteurId(sujetDto.getAuteurId())
                .auteurNom(user.getNom() + " " + user.getPrenom())
                .forum(forum)
                .resolu(false)
                .epingle(false)
                .verrouille(false)
                .nbVues(0)
                .build();

        Sujet savedSujet = sujetRepository.save(sujet);
        return mapToDto(savedSujet);
    }

    @Override
    @Transactional
    public SujetDto getSujetById(Long id, Long utilisateurId) {
        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        // Incrémenter le nombre de vues seulement si l'utilisateur n'est pas l'auteur
        if (utilisateurId != null && !utilisateurId.equals(sujet.getAuteurId())) {
            sujet.incrementerVues();
            sujetRepository.save(sujet);
        }

        return mapToDto(sujet);
    }

    @Override
    public Page<SujetDto> getSujetsByForumId(Long forumId, Pageable pageable) {
        return sujetRepository.findByForumIdOrderByEpingleDescDateCreationDesc(forumId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<SujetDto> rechercherSujets(Long forumId, String searchTerm, Pageable pageable) {
        return sujetRepository.rechercherSujets(forumId, searchTerm, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<SujetDto> getSujetsByAuteurId(Long auteurId) {
        return sujetRepository.findByAuteurId(auteurId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SujetDto> getSujetsResolus(Long forumId, Pageable pageable) {
        return sujetRepository.findByForumIdAndResoluTrue(forumId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public SujetDto updateSujet(Long id, SujetDto sujetDto, Long utilisateurId) {
        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        // Vérifier que l'utilisateur est l'auteur du sujet
        if (!sujet.getAuteurId().equals(utilisateurId)) {
            throw new UnauthorizedException("Seul l'auteur peut modifier ce sujet");
        }

        // Vérifier que le sujet n'est pas verrouillé
        if (sujet.isVerrouille()) {
            throw new UnauthorizedException("Ce sujet est verrouillé et ne peut pas être modifié");
        }

        sujet.setTitre(sujetDto.getTitre());
        sujet.setContenu(sujetDto.getContenu());

        Sujet updatedSujet = sujetRepository.save(sujet);
        return mapToDto(updatedSujet);
    }

    @Override
    @Transactional
    public void deleteSujet(Long id, Long utilisateurId) {
        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        // Vérifier que l'utilisateur est l'auteur ou a les droits de modération
        if (!sujet.getAuteurId().equals(utilisateurId) && !estModerateur(utilisateurId)) {
            throw new UnauthorizedException("Vous n'avez pas le droit de supprimer ce sujet");
        }

        sujetRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void marquerResolu(Long id, Long utilisateurId) {
        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        // Seul l'auteur peut marquer son sujet comme résolu
        if (!sujet.getAuteurId().equals(utilisateurId)) {
            throw new UnauthorizedException("Seul l'auteur peut marquer ce sujet comme résolu");
        }

        sujet.setResolu(true);
        sujetRepository.save(sujet);
    }

    @Override
    @Transactional
    public void epingler(Long id, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent épingler des sujets");
        }

        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        sujet.setEpingle(true);
        sujetRepository.save(sujet);
    }

    @Override
    @Transactional
    public void desepingler(Long id, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent désépingler des sujets");
        }

        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        sujet.setEpingle(false);
        sujetRepository.save(sujet);
    }

    @Override
    @Transactional
    public void verrouiller(Long id, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent verrouiller des sujets");
        }

        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        sujet.setVerrouille(true);
        sujetRepository.save(sujet);
    }

    @Override
    @Transactional
    public void deverrouiller(Long id, Long moderateurId) {
        if (!estModerateur(moderateurId)) {
            throw new UnauthorizedException("Seuls les modérateurs peuvent déverrouiller des sujets");
        }

        Sujet sujet = sujetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sujet", "id", id));

        sujet.setVerrouille(false);
        sujetRepository.save(sujet);
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

    // Méthode utilitaire pour mapper un Sujet en SujetDto
    private SujetDto mapToDto(Sujet sujet) {
        Integer nbMessages = messageRepository.countBySujetId(sujet.getId());

        return SujetDto.builder()
                .id(sujet.getId())
                .titre(sujet.getTitre())
                .contenu(sujet.getContenu())
                .auteurId(sujet.getAuteurId())
                .auteurNom(sujet.getAuteurNom())
                .forumId(sujet.getForum().getId())
                .resolu(sujet.isResolu())
                .epingle(sujet.isEpingle())
                .verrouille(sujet.isVerrouille())
                .nbVues(sujet.getNbVues())
                .nbMessages(nbMessages)
                .dateCreation(sujet.getDateCreation())
                .dateMiseAJour(sujet.getDateMiseAJour())
                .build();
    }
}
