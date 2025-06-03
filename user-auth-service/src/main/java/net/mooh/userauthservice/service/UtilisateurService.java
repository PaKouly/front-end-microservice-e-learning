package net.mooh.userauthservice.service;

import net.mooh.userauthservice.dtos.UtilisateurCreationDto;
import net.mooh.userauthservice.dtos.UtilisateurDto;

import java.util.List;

public interface UtilisateurService {
    UtilisateurDto creerUtilisateur(UtilisateurCreationDto utilisateurCreationDto);

    UtilisateurDto getUtilisateurById(Long id);

    UtilisateurDto getUtilisateurByEmail(String email);

    List<UtilisateurDto> getAllUtilisateurs();

    List<UtilisateurDto> getUtilisateursByRole(String roleName);

    UtilisateurDto updateUtilisateur(Long id, UtilisateurDto utilisateurDto);

    void deleteUtilisateur(Long id);

    void activerUtilisateur(Long id);

    void desactiverUtilisateur(Long id);

    void ajouterRoleUtilisateur(Long utilisateurId, String roleName);

    void retirerRoleUtilisateur(Long utilisateurId, String roleName);

    List<UtilisateurDto> rechercherUtilisateurs(String searchTerm);
}
