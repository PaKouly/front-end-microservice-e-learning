package net.mooh.forumservice.client;

import lombok.*;

import java.time.LocalDateTime;

@Setter@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormationDto {

    private Long id;
    private String titre;
    private String description;
    private String niveau;
    private Integer duree;
    private String langue;
    private boolean publique;
    private boolean active;
    private Long createurId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateMiseAJour;
}
