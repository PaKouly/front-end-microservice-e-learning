package net.mooh.evaluationservice.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "choix_reponses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChoixReponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String texte;

    private boolean correct;

    private Integer ordre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
}