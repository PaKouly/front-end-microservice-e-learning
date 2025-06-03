package net.mooh.forumservice.client;

import org.springframework.stereotype.Component;

@Component
public class ContentClientFallback implements ContentClient {

    @Override
    public FormationDto getFormationById(Long id) {
        // Renvoyer un DTO minimal en cas d'erreur
        return FormationDto.builder()
                .id(id)
                .titre("Formation indisponible")
                .build();
    }
}
