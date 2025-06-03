package net.mooh.userauthservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.NotificationDto;
import net.mooh.userauthservice.entities.Notification;
import net.mooh.userauthservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {


    private final NotificationService notificationService;

    @PostMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<NotificationDto> creerNotification(
            @PathVariable Long utilisateurId,
            @RequestParam String titre,
            @RequestParam String contenu,
            @RequestParam Notification.NotificationType type) {
        return new ResponseEntity<>(
                notificationService.creerNotification(utilisateurId, titre, contenu, type),
                HttpStatus.CREATED);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<NotificationDto>> getNotificationsForUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUtilisateur(utilisateurId));
    }

    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    public ResponseEntity<List<NotificationDto>> getNotificationsNonLues(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(utilisateurId));
    }

    @GetMapping("/utilisateur/{utilisateurId}/count-non-lues")
    public ResponseEntity<Long> countNotificationsNonLues(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(notificationService.countNotificationsNonLues(utilisateurId));
    }

    @PatchMapping("/{notificationId}/marquer-lue")
    public ResponseEntity<Void> marquerCommeLue(@PathVariable Long notificationId) {
        notificationService.marquerCommeLue(notificationId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/utilisateur/{utilisateurId}/marquer-toutes-lues")
    public ResponseEntity<Void> marquerToutesCommeLues(@PathVariable Long utilisateurId) {
        notificationService.marquerToutesCommeLues(utilisateurId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> supprimerNotification(@PathVariable Long notificationId) {
        notificationService.supprimerNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
