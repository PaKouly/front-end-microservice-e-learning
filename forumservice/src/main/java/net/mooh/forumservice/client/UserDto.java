package net.mooh.forumservice.client;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String langue;
    private boolean actif;
    private Set<String> roles = new HashSet<>();
}
