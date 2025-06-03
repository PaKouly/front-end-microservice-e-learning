package net.mooh.evaluationservice.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(length = 1000)
    private String description;

    @Column(name = "formation_id")
    private Long formationId;

    @Column(name = "module_id")
    private Long moduleId;

    @Column(name = "createur_id", nullable = false)
    private Long createurId;

    private Integer duree; // en minutes

    private Integer noteMinimale; // note minimale pour réussir

    private Integer nombreTentativesMax;

    private boolean melangQuestions;

    private boolean melangReponses;

    private boolean affichageResultatImmediat;

    private boolean actif;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Evaluation> evaluations = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    // Méthode utilitaire pour ajouter une question
    public void ajouterQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    // Méthode utilitaire pour supprimer une question
    public void supprimerQuestion(Question question) {
        questions.remove(question);
        question.setQuiz(null);
    }

    // Méthode utilitaire pour calculer le nombre total de points
    public Integer getTotalPoints() {
        if (questions == null || questions.isEmpty()) {
            return 0;
        }
        return questions.stream()
                .mapToInt(question -> question.getPoints() != null ? question.getPoints() : 0)
                .sum();
    }
}