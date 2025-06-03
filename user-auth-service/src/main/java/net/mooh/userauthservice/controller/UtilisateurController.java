package net.mooh.userauthservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.dtos.UtilisateurCreationDto;
import net.mooh.userauthservice.dtos.UtilisateurDto;
import net.mooh.userauthservice.service.UtilisateurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping
    public ResponseEntity<UtilisateurDto> creerUtilisateur( @RequestBody UtilisateurCreationDto utilisateurDto) {
        return new ResponseEntity<>(utilisateurService.creerUtilisateur(utilisateurDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDto> getUtilisateurById(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UtilisateurDto> getUtilisateurByEmail(@PathVariable String email) {
        return ResponseEntity.ok(utilisateurService.getUtilisateurByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<UtilisateurDto>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UtilisateurDto>> getUtilisateursByRole(@PathVariable String roleName) {
        return ResponseEntity.ok(utilisateurService.getUtilisateursByRole(roleName));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDto> updateUtilisateur(
            @PathVariable Long id,
             @RequestBody UtilisateurDto utilisateurDto) {
        return ResponseEntity.ok(utilisateurService.updateUtilisateur(id, utilisateurDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activerUtilisateur(@PathVariable Long id) {
        utilisateurService.activerUtilisateur(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiverUtilisateur(@PathVariable Long id) {
        utilisateurService.desactiverUtilisateur(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Void> ajouterRoleUtilisateur(
            @PathVariable Long id,
            @PathVariable String roleName) {
        utilisateurService.ajouterRoleUtilisateur(id, roleName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/roles/{roleName}")
    public ResponseEntity<Void> retirerRoleUtilisateur(
            @PathVariable Long id,
            @PathVariable String roleName) {
        utilisateurService.retirerRoleUtilisateur(id, roleName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UtilisateurDto>> rechercherUtilisateurs(@RequestParam String term) {
        return ResponseEntity.ok(utilisateurService.rechercherUtilisateurs(term));
    }
}
