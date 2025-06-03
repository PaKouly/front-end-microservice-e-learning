package net.mooh.userauthservice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Setter@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    private Set<Utilisateur> utilisateurs = new HashSet<>();

    // Méthode utilitaire pour ajouter une permission
    public void ajouterPermission(String permission) {
        this.permissions.add(permission);
    }

    // Méthode utilitaire pour supprimer une permission
    public void supprimerPermission(String permission) {
        this.permissions.remove(permission);
    }

    // Méthode utilitaire pour vérifier si le rôle a une permission spécifique
    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }

}
