package net.mooh.userauthservice.service;

import net.mooh.userauthservice.dtos.NotificationDto;
import net.mooh.userauthservice.entities.Notification;

import java.util.List;

public interface NotificationService {
    NotificationDto creerNotification(Long utilisateurId, String titre, String contenu, Notification.NotificationType type);

    List<NotificationDto> getNotificationsForUtilisateur(Long utilisateurId);

    List<NotificationDto> getNotificationsNonLues(Long utilisateurId);

    void marquerCommeLue(Long notificationId);

    void marquerToutesCommeLues(Long utilisateurId);

    void supprimerNotification(Long notificationId);

    long countNotificationsNonLues(Long utilisateurId);
}
