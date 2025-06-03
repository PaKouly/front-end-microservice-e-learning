package net.mooh.contentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-auth-service", fallback = UserClientFallback.class)
public interface UserClient {
    @GetMapping("/api/utilisateurs/{id}")
    ResponseEntity<UserDto> getUtilisateurById(@PathVariable Long id);

    @GetMapping("/api/roles/{nom}")
    ResponseEntity<RoleDto> getRoleByNom(@PathVariable String nom);

}
