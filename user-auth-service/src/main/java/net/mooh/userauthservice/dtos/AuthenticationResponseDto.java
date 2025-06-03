package net.mooh.userauthservice.dtos;

import lombok.*;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthenticationResponseDto {
    private Long userId;
    private String email;
    private String nom;
    private String prenom;
    private Set<String> roles;
    private boolean authenticated;
    private String message;
}
