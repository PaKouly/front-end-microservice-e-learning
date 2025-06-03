package net.mooh.userauthservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.RoleDto;
import net.mooh.userauthservice.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {


    private final RoleService roleService;

    @PostMapping
    public ResponseEntity<RoleDto> creerRole( @RequestBody RoleDto roleDto) {
        return new ResponseEntity<>(roleService.creerRole(roleDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDto> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping("/nom/{nom}")
    public ResponseEntity<RoleDto> getRoleByNom(@PathVariable String nom) {
        return ResponseEntity.ok(roleService.getRoleByNom(nom));
    }

    @GetMapping
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDto> updateRole(
            @PathVariable Long id,
             @RequestBody RoleDto roleDto) {
        return ResponseEntity.ok(roleService.updateRole(id, roleDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Void> ajouterPermission(
            @PathVariable Long id,
            @RequestParam String permission) {
        roleService.ajouterPermission(id, permission);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/permissions")
    public ResponseEntity<Void> supprimerPermission(
            @PathVariable Long id,
            @RequestParam String permission) {
        roleService.supprimerPermission(id, permission);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{nom}/permissions")
    public ResponseEntity<Set<String>> getPermissionsByRoleNom(@PathVariable String nom) {
        return ResponseEntity.ok(roleService.getPermissionsByRoleNom(nom));
    }
}
