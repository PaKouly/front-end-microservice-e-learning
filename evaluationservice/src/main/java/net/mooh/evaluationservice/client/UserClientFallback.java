package net.mooh.evaluationservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {
    @Override
    public ResponseEntity<UserDto> getUtilisateurById(Long id) {
        return ResponseEntity.ok(UserDto.builder()
                .id(id)
                .nom("Utilisateur")
                .prenom("Indisponible")
                .email("service.indisponible@example.com")
                .build());
    }
}