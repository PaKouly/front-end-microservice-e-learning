package net.mooh.userauthservice.config;

import lombok.RequiredArgsConstructor;
import net.mooh.userauthservice.entities.Role;
import net.mooh.userauthservice.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializationConfig {


    private final RoleRepository roleRepository;

    @Bean
    public CommandLineRunner initRoles() {
        return args -> {
            // Vérifie si les rôles existent déjà
            if (roleRepository.count() == 0) {
                // Permissions communes
                HashSet<String> permissionsApprenant = new HashSet<>(Arrays.asList(
                        "SUIVRE_FORMATION",
                        "CONSULTER_FORMATION",
                        "PASSER_QUIZ",
                        "CONSULTER_RESULTAT",
                        "CONSULTER_CERTIFICATION",
                        "PARTICIPER_FORUM"
                ));

                HashSet<String> permissionsFormateur = new HashSet<>(Arrays.asList(
                        "CONSULTER_FORMATION",
                        "CREER_FORMATION",
                        "MODIFIER_FORMATION",
                        "CREER_MODULE",
                        "MODIFIER_MODULE",
                        "CREER_CONTENU",
                        "MODIFIER_CONTENU",
                        "CREER_QUIZ",
                        "MODIFIER_QUIZ",
                        "CONSULTER_RESULTAT",
                        "NOTER_EVALUATION",
                        "CREER_FORUM",
                        "MODERER_FORUM",
                        "PARTICIPER_FORUM"
                ));

                HashSet<String> permissionsAdmin = new HashSet<>(Arrays.asList(
                        "GERER_UTILISATEUR",
                        "GERER_ROLE",
                        "CONSULTER_FORMATION",
                        "CREER_FORMATION",
                        "MODIFIER_FORMATION",
                        "SUPPRIMER_FORMATION",
                        "GERER_CERTIFICATION",
                        "CONSULTER_STATISTIQUE",
                        "MODERER_FORUM",
                        "PARTICIPER_FORUM"
                ));

                HashSet<String> permissionsGestionnaire = new HashSet<>(Arrays.asList(
                        "CONSULTER_FORMATION",
                        "GERER_INSCRIPTION",
                        "GERER_CERTIFICATION",
                        "CONSULTER_STATISTIQUE",
                        "GENERER_RAPPORT",
                        "CONSULTER_UTILISATEUR",
                        "PARTICIPER_FORUM"
                ));

                // Création des rôles avec leurs permissions
                List<Role> defaultRoles = Arrays.asList(
                        Role.builder()
                                .nom("APPRENANT")
                                .description("Utilisateur qui suit des formations")
                                .permissions(permissionsApprenant)
                                .build(),
                        Role.builder()
                                .nom("FORMATEUR")
                                .description("Enseignant qui crée et anime des formations")
                                .permissions(permissionsFormateur)
                                .build(),
                        Role.builder()
                                .nom("ADMINISTRATEUR")
                                .description("Administrateur de la plateforme avec tous les droits")
                                .permissions(permissionsAdmin)
                                .build(),
                        Role.builder()
                                .nom("GESTIONNAIRE")
                                .description("Gestionnaire administratif de la plateforme")
                                .permissions(permissionsGestionnaire)
                                .build()
                );

                roleRepository.saveAll(defaultRoles);

                System.out.println("Initialisation des rôles par défaut terminée");
            }
        };
    }
}
