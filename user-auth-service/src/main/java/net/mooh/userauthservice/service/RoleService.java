package net.mooh.userauthservice.service;

import net.mooh.userauthservice.dtos.RoleDto;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleDto creerRole(RoleDto roleDto);

    RoleDto getRoleById(Long id);

    RoleDto getRoleByNom(String nom);

    List<RoleDto> getAllRoles();

    RoleDto updateRole(Long id, RoleDto roleDto);

    void deleteRole(Long id);

    void ajouterPermission(Long roleId, String permission);

    void supprimerPermission(Long roleId, String permission);

    Set<String> getPermissionsByRoleNom(String roleName);
}
