package net.mooh.certificationservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContentClientFallback implements ContentClient {
    @Override
    public ResponseEntity<FormationDto> getFormationById(Long id) {
        return ResponseEntity.ok(FormationDto.builder()
                .id(id)
                .titre("Formation indisponible")
                .build());
    }

    @Override
    public ResponseEntity<List<ProgressionApprenantDto>> getProgressionsByApprenantId(Long apprenantId) {
        return ResponseEntity.ok(new ArrayList<>());
    }
}