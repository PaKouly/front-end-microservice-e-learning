package net.mooh.userauthservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.NotificationDto;
import net.mooh.userauthservice.entities.Notification;
import net.mooh.userauthservice.entities.Utilisateur;
import net.mooh.userauthservice.exception.ResourceNotFoundException;
import net.mooh.userauthservice.repository.NotificationRepository;
import net.mooh.userauthservice.repository.UtilisateurRepository;
import net.mooh.userauthservice.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {


    private final NotificationRepository notificationRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Override
    @Transactional
    public NotificationDto creerNotification(Long utilisateurId, String titre, String contenu, Notification.NotificationType type) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", utilisateurId));

        Notification notification = Notification.builder()
                .titre(titre)
                .contenu(contenu)
                .type(type)
                .lue(false)
                .utilisateur(utilisateur)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return mapToDto(savedNotification);
    }

    @Override
    public List<NotificationDto> getNotificationsForUtilisateur(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", utilisateurId));

        return notificationRepository.findByUtilisateurOrderByDateCreationDesc(utilisateur).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getNotificationsNonLues(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", utilisateurId));

        return notificationRepository.findByUtilisateurAndLueOrderByDateCreationDesc(utilisateur, false).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void marquerCommeLue(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        notification.setLue(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void marquerToutesCommeLues(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", utilisateurId));

        List<Notification> notifications = notificationRepository.findByUtilisateurAndLueOrderByDateCreationDesc(utilisateur, false);

        for (Notification notification : notifications) {
            notification.setLue(true);
        }

        notificationRepository.saveAll(notifications);
    }

    @Override
    @Transactional
    public void supprimerNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification", "id", notificationId);
        }

        notificationRepository.deleteById(notificationId);
    }

    @Override
    public long countNotificationsNonLues(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", utilisateurId));

        return notificationRepository.countByUtilisateurAndLue(utilisateur, false);
    }

    // Méthode utilitaire pour mapper une Notification en NotificationDto
    private NotificationDto mapToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .titre(notification.getTitre())
                .contenu(notification.getContenu())
                .type(notification.getType())
                .lue(notification.isLue())
                .dateCreation(notification.getDateCreation())
                .build();
    }
}
