package net.mooh.userauthservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.RoleDto;
import net.mooh.userauthservice.entities.Role;
import net.mooh.userauthservice.exception.ResourceNotFoundException;
import net.mooh.userauthservice.repository.RoleRepository;
import net.mooh.userauthservice.service.RoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public RoleDto creerRole(RoleDto roleDto) {
        if (roleRepository.existsByNom(roleDto.getNom())) {
            throw new IllegalArgumentException("Un rôle avec ce nom existe déjà");
        }

        Role role = Role.builder()
                .nom(roleDto.getNom())
                .description(roleDto.getDescription())
                .permissions(roleDto.getPermissions())
                .build();

        Role savedRole = roleRepository.save(role);
        return mapToDto(savedRole);
    }

    @Override
    public RoleDto getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        return mapToDto(role);
    }

    @Override
    public RoleDto getRoleByNom(String nom) {
        Role role = roleRepository.findByNom(nom)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "nom", nom));

        return mapToDto(role);
    }

    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleDto updateRole(Long id, RoleDto roleDto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        // Vérifier si le nom est déjà utilisé par un autre rôle
        if (!role.getNom().equals(roleDto.getNom()) &&
                roleRepository.existsByNom(roleDto.getNom())) {
            throw new IllegalArgumentException("Un rôle avec ce nom existe déjà");
        }

        role.setNom(roleDto.getNom());
        role.setDescription(roleDto.getDescription());

        // Mettre à jour les permissions si fournies
        if (roleDto.getPermissions() != null) {
            role.setPermissions(roleDto.getPermissions());
        }

        Role updatedRole = roleRepository.save(role);
        return mapToDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role", "id", id);
        }

        // Note: En réalité, il faudrait vérifier si des utilisateurs ont ce rôle
        // avant de le supprimer ou implémenter une stratégie de suppression appropriée
        roleRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void ajouterPermission(Long roleId, String permission) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        role.ajouterPermission(permission);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void supprimerPermission(Long roleId, String permission) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", roleId));

        role.supprimerPermission(permission);
        roleRepository.save(role);
    }

    @Override
    public Set<String> getPermissionsByRoleNom(String roleName) {
        Role role = roleRepository.findByNom(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "nom", roleName));

        return role.getPermissions();
    }

    // Méthode utilitaire pour mapper un Role en RoleDto
    private RoleDto mapToDto(Role role) {
        return RoleDto.builder()
                .id(role.getId())
                .nom(role.getNom())
                .description(role.getDescription())
                .permissions(role.getPermissions())
                .build();
    }
}
