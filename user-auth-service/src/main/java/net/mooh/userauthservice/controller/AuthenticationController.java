package net.mooh.userauthservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.AuthenticationRequestDto;
import net.mooh.userauthservice.dtos.AuthenticationResponseDto;
import net.mooh.userauthservice.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login( @RequestBody AuthenticationRequestDto request) {
        AuthenticationResponseDto response = authenticationService.authenticate(request);

        if (response.isAuthenticated()) {
            return ResponseEntity.ok(response);
        } else {
            // Retourner 401 Unauthorized pour une authentification échouée
            return ResponseEntity.status(401).body(response);
        }
    }
}
