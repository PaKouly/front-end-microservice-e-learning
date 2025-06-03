package net.mooh.forumservice.client;

import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDto {

    private Long id;
    private String nom;
    private String description;
    private Set<String> permissions = new HashSet<>();
}
