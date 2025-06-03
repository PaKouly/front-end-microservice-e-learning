package net.mooh.userauthservice.dtos;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AuthenticationRequestDto {
    private String email;
    private String motDePasse;
}
