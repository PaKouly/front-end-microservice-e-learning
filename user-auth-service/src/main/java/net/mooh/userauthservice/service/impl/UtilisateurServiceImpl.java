package net.mooh.userauthservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.UtilisateurCreationDto;
import net.mooh.userauthservice.dtos.UtilisateurDto;
import net.mooh.userauthservice.entities.Role;
import net.mooh.userauthservice.entities.Utilisateur;
import net.mooh.userauthservice.exception.ResourceNotFoundException;
import net.mooh.userauthservice.repository.NotificationRepository;
import net.mooh.userauthservice.repository.RoleRepository;
import net.mooh.userauthservice.repository.UtilisateurRepository;
import net.mooh.userauthservice.service.UtilisateurService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilisateurServiceImpl implements UtilisateurService {
    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public UtilisateurDto creerUtilisateur(UtilisateurCreationDto utilisateurCreationDto) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(utilisateurCreationDto.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        // Créer le nouvel utilisateur
        Utilisateur utilisateur = Utilisateur.builder()
                .nom(utilisateurCreationDto.getNom())
                .prenom(utilisateurCreationDto.getPrenom())
                .email(utilisateurCreationDto.getEmail())
                .motDePasse(utilisateurCreationDto.getMotDePasse()) // Note: en réalité, on hasherait le mot de passe ici
                .langue(utilisateurCreationDto.getLangue())
                .actif(true)
                .roles(new HashSet<>())
                .build();

        // Ajouter les rôles
        if (utilisateurCreationDto.getRoles() != null && !utilisateurCreationDto.getRoles().isEmpty()) {
            for (String roleName : utilisateurCreationDto.getRoles()) {
                Role role = roleRepository.findByNom(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "nom", roleName));
                utilisateur.ajouterRole(role);
            }
        } else {
            // Si aucun rôle n'est spécifié, ajouter le rôle par défaut "APPRENANT"
            Role defaultRole = roleRepository.findByNom("APPRENANT")
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "nom", "APPRENANT"));
            utilisateur.ajouterRole(defaultRole);
        }

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        return mapToDto(savedUtilisateur);
    }

    @Override
    public UtilisateurDto getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        return mapToDto(utilisateur);
    }

    @Override
    public UtilisateurDto getUtilisateurByEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "email", email));

        return mapToDto(utilisateur);
    }

    @Override
    public List<UtilisateurDto> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UtilisateurDto> getUtilisateursByRole(String roleName) {
        return utilisateurRepository.findByRoleName(roleName).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UtilisateurDto updateUtilisateur(Long id, UtilisateurDto utilisateurDto) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        // Mettre à jour les champs de base
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setLangue(utilisateurDto.getLangue());

        // Note: On ne met pas à jour l'email et le mot de passe ici
        // Ces opérations nécessiteraient des validations supplémentaires

        Utilisateur updatedUtilisateur = utilisateurRepository.save(utilisateur);
        return mapToDto(updatedUtilisateur);
    }

    @Override
    @Transactional
    public void deleteUtilisateur(Long id) {
        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur", "id", id);
        }
        utilisateurRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activerUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        utilisateur.setActif(true);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public void desactiverUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        utilisateur.setActif(false);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public void ajouterRoleUtilisateur(Long utilisateurId, String roleName) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", utilisateurId));

        Role role = roleRepository.findByNom(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "nom", roleName));

        utilisateur.ajouterRole(role);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    @Transactional
    public void retirerRoleUtilisateur(Long utilisateurId, String roleName) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", utilisateurId));

        Role role = roleRepository.findByNom(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "nom", roleName));

        utilisateur.getRoles().remove(role);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public List<UtilisateurDto> rechercherUtilisateurs(String searchTerm) {
        return utilisateurRepository.findByNomContainingOrPrenomContaining(searchTerm, searchTerm).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Méthode utilitaire pour mapper un Utilisateur en UtilisateurDto
    private UtilisateurDto mapToDto(Utilisateur utilisateur) {
        Set<String> roleNames = utilisateur.getRoles().stream()
                .map(Role::getNom)
                .collect(Collectors.toSet());

        Long nonLues = notificationRepository.countByUtilisateurAndLue(utilisateur, false);

        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .langue(utilisateur.getLangue())
                .actif(utilisateur.isActif())
                .roles(roleNames)
                .nonLues(nonLues)
                .build();
    }
}
