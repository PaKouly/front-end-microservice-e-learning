package net.mooh.contentservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient{
    @Override
    public ResponseEntity<UserDto> getUtilisateurById(Long id) {
        // Retourner un utilisateur par défaut ou null en cas d'échec
        return ResponseEntity.ok(UserDto.builder()
                .id(id)
                .nom("Indisponible")
                .prenom("Service")
                .email("service.indisponible@example.com")
                .build());
    }

    @Override
    public ResponseEntity<RoleDto> getRoleByNom(String nom) {
        // Retourner un rôle par défaut ou null en cas d'échec
        return ResponseEntity.ok(RoleDto.builder()
                .id(0L)
                .nom(nom)
                .description("Service utilisateur indisponible")
                .build());
    }
}
