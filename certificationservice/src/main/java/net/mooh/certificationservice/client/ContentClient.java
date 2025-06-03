package net.mooh.certificationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "contentservice", fallback = ContentClientFallback.class)
public interface ContentClient {
    @GetMapping("/api/formations/{id}")
    ResponseEntity<FormationDto> getFormationById(@PathVariable Long id);

    @GetMapping("/api/progressions/apprenant/{apprenantId}")
    ResponseEntity<List<ProgressionApprenantDto>> getProgressionsByApprenantId(@PathVariable Long apprenantId);
}