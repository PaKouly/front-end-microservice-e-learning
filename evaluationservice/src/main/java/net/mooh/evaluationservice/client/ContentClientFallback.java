package net.mooh.evaluationservice.client;

import org.springframework.stereotype.Component;

@Component
public class ContentClientFallback implements ContentClient {
    @Override
    public FormationDto getFormationById(Long id) {
        return FormationDto.builder()
                .id(id)
                .titre("Formation indisponible")
                .build();
    }

    @Override
    public SectionFormationDto getSectionById(Long id) {
        return SectionFormationDto.builder()
                .id(id)
                .titre("Section indisponible")
                .build();
    }
}