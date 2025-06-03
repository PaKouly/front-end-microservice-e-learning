package net.mooh.evaluationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "contentservice", fallback = ContentClientFallback.class)
public interface ContentClient {
    @GetMapping("/api/formations/{id}")
    FormationDto getFormationById(@PathVariable Long id);

    @GetMapping("/api/sections/{id}")
    SectionFormationDto getSectionById(@PathVariable Long id);
}