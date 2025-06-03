package net.mooh.userauthservice.dtos;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurDto {
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String langue;
    private String motDePasse;
    private boolean actif;
    private Set<String> roles = new HashSet<>();

    private Long nonLues; // Nombre de notifications non lues
}
