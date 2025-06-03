package net.mooh.forumservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.client.ContentClient;
import net.mooh.forumservice.dtos.ForumDto;
import net.mooh.forumservice.entities.Forum;
import net.mooh.forumservice.entities.Signalement.StatutSignalement;
import net.mooh.forumservice.exception.DuplicateResourceException;
import net.mooh.forumservice.exception.ResourceNotFoundException;
import net.mooh.forumservice.repository.ForumRepository;
import net.mooh.forumservice.repository.MessageRepository;
import net.mooh.forumservice.repository.SignalementRepository;
import net.mooh.forumservice.repository.SujetRepository;
import net.mooh.forumservice.service.ForumService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final ForumRepository forumRepository;
    private final SujetRepository sujetRepository;
    private final MessageRepository messageRepository;
    private final SignalementRepository signalementRepository;
    @Qualifier("net.mooh.forumservice.client.ContentClient")
    private final ContentClient contentClient;

    @Override
    @Transactional
    public ForumDto creerForum(ForumDto forumDto) {
        // Vérifier si un forum existe déjà pour cette formation
        if (forumRepository.existsByFormationId(forumDto.getFormationId())) {
            throw new DuplicateResourceException("Un forum existe déjà pour cette formation");
        }

        // Vérifier si la formation existe
        try {
            contentClient.getFormationById(forumDto.getFormationId());
        } catch (Exception e) {
            throw new ResourceNotFoundException("Formation", "id", forumDto.getFormationId());
        }

        Forum forum = Forum.builder()
                .titre(forumDto.getTitre())
                .description(forumDto.getDescription())
                .formationId(forumDto.getFormationId())
                .actif(true)
                .build();

        Forum savedForum = forumRepository.save(forum);
        return mapToDto(savedForum);
    }

    @Override
    public ForumDto getForumById(Long id) {
        Forum forum = forumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", id));

        return mapToDto(forum);
    }

    @Override
    public ForumDto getForumByFormationId(Long formationId) {
        Forum forum = forumRepository.findByFormationId(formationId)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "formationId", formationId));

        return mapToDto(forum);
    }

    @Override
    public List<ForumDto> getAllForums() {
        return forumRepository.findByActifTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ForumDto updateForum(Long id, ForumDto forumDto) {
        Forum forum = forumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", id));

        forum.setTitre(forumDto.getTitre());
        forum.setDescription(forumDto.getDescription());

        Forum updatedForum = forumRepository.save(forum);
        return mapToDto(updatedForum);
    }

    @Override
    @Transactional
    public void deleteForum(Long id) {
        if (!forumRepository.existsById(id)) {
            throw new ResourceNotFoundException("Forum", "id", id);
        }

        forumRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activerForum(Long id) {
        Forum forum = forumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", id));

        forum.setActif(true);
        forumRepository.save(forum);
    }

    @Override
    @Transactional
    public void desactiverForum(Long id) {
        Forum forum = forumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forum", "id", id));

        forum.setActif(false);
        forumRepository.save(forum);
    }

    // Méthode utilitaire pour mapper un Forum en ForumDto
    private ForumDto mapToDto(Forum forum) {
        // Récupérer les statistiques
        Integer nbSujets = sujetRepository.countByForumId(forum.getId());
        Integer nbMessages = messageRepository.countByForumId(forum.getId());
        Integer nbSignalementsNonResolus = signalementRepository.countByForumIdAndStatut(
                forum.getId(), StatutSignalement.NOUVEAU);

        // Récupérer le titre de la formation si disponible
        String formationTitre = null;
        try {
            formationTitre = contentClient.getFormationById(forum.getFormationId()).getTitre();
        } catch (Exception e) {
            // Ignorer les erreurs, le titre de la formation n'est pas critique
        }

        return ForumDto.builder()
                .id(forum.getId())
                .titre(forum.getTitre())
                .description(forum.getDescription())
                .formationId(forum.getFormationId())
                .formationTitre(formationTitre)
                .actif(forum.isActif())
                .dateCreation(forum.getDateCreation())
                .dateMiseAJour(forum.getDateMiseAJour())
                .nbSujets(nbSujets)
                .nbMessages(nbMessages)
                .nbSignalementsNonResolus(nbSignalementsNonResolus)
                .build();
    }
}
