package net.mooh.userauthservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.AuthenticationRequestDto;
import net.mooh.userauthservice.dtos.AuthenticationResponseDto;
import net.mooh.userauthservice.entities.Role;
import net.mooh.userauthservice.entities.Utilisateur;
import net.mooh.userauthservice.repository.UtilisateurRepository;
import net.mooh.userauthservice.service.AuthenticationService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {


    private final UtilisateurRepository utilisateurRepository;

    @Override
    public AuthenticationResponseDto authenticate(AuthenticationRequestDto request) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(request.getEmail());

        // Si utilisateur non trouvé, retourner échec d'authentification
        if (utilisateurOpt.isEmpty()) {
            return AuthenticationResponseDto.builder()
                    .authenticated(false)
                    .message("Utilisateur non trouvé")
                    .build();
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // Si utilisateur inactif, retourner échec d'authentification
        if (!utilisateur.isActif()) {
            return AuthenticationResponseDto.builder()
                    .authenticated(false)
                    .message("Compte utilisateur désactivé")
                    .build();
        }

        // Vérifier le mot de passe
        // Note: Dans une implémentation réelle, vous utiliseriez BCrypt ou un autre algorithme de hachage
        if (!utilisateur.getMotDePasse().equals(request.getMotDePasse())) {
            return AuthenticationResponseDto.builder()
                    .authenticated(false)
                    .message("Mot de passe incorrect")
                    .build();
        }

        // Authentification réussie, retourner les informations utilisateur
        Set<String> roles = utilisateur.getRoles().stream()
                .map(Role::getNom)
                .collect(Collectors.toSet());

        return AuthenticationResponseDto.builder()
                .userId(utilisateur.getId())
                .email(utilisateur.getEmail())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .roles(roles)
                .authenticated(true)
                .message("Authentification réussie")
                .build();
    }
}
