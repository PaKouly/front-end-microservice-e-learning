package net.mooh.contentservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contenus")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    @Enumerated(EnumType.STRING)

    private TypeContenu type;

    private String url;

    @Column(length = 10000)

    private String texte;

    private Integer ordre;

    private String langue;

    private String format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id")
    @ToString.Exclude
    private SectionFormation section;

    private Integer dureeEstimee; // en minutes

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Enum pour les types de contenu
    public enum TypeContenu {
        VIDEO,
        DOCUMENT,
        TEXTE,
        QUIZ,
        EXERCICE,
        LIEN,
        IMAGE
    }
}
