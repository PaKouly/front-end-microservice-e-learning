package net.mooh.forumservice.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public ResponseEntity<UserDto> getUtilisateurById(Long id) {
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<RoleDto> getRoleByNom(String nom) {
        return ResponseEntity.notFound().build();
    }
}
