package net.mooh.forumservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "contentservice", fallback = ContentClientFallback.class)
public interface ContentClient {

    @GetMapping("/api/formations/{id}")
    FormationDto getFormationById(@PathVariable Long id);
}

//https://claude.ai/chat/34c3582c-51e9-484b-8c42-72e478263af5
