package net.mooh.userauthservice.dtos;

import lombok.*;
import net.mooh.userauthservice.entities.Notification;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;

    private String titre;

    private String contenu;

    private Notification.NotificationType type;

    private boolean lue;

    private LocalDateTime dateCreation;
}
