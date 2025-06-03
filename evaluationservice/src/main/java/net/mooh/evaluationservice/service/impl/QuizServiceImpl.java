package net.mooh.evaluationservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.evaluationservice.client.ContentClient;
import net.mooh.evaluationservice.client.UserClient;
import net.mooh.evaluationservice.client.UserDto;
import net.mooh.evaluationservice.dtos.QuizDto;
import net.mooh.evaluationservice.entities.Quiz;
import net.mooh.evaluationservice.exception.ResourceNotFoundException;
import net.mooh.evaluationservice.exception.UnauthorizedException;
import net.mooh.evaluationservice.repository.QuestionRepository;
import net.mooh.evaluationservice.repository.QuizRepository;
import net.mooh.evaluationservice.service.QuizService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    @Qualifier("net.mooh.evaluationservice.client.UserClient")
    private final UserClient userClient;
    @Qualifier("net.mooh.evaluationservice.client.ContentClient")
    private final ContentClient contentClient;

    @Override
    @Transactional
    public QuizDto creerQuiz(QuizDto quizDto) {
        // Vérifier que l'utilisateur existe et a le rôle FORMATEUR
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(quizDto.getCreateurId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", quizDto.getCreateurId());
        }

        UserDto user = userResponse.getBody();
        if (!user.getRoles().contains("FORMATEUR") && !user.getRoles().contains("ADMINISTRATEUR")) {
            throw new UnauthorizedException("L'utilisateur n'a pas le rôle nécessaire pour créer un quiz");
        }

        // Vérifier que la formation/module existe
        if (quizDto.getFormationId() != null) {
            try {
                contentClient.getFormationById(quizDto.getFormationId());
            } catch (Exception e) {
                throw new ResourceNotFoundException("Formation", "id", quizDto.getFormationId());
            }
        }

        Quiz quiz = mapToEntity(quizDto);
        quiz.setActif(true);
        Quiz savedQuiz = quizRepository.save(quiz);
        return mapToDto(savedQuiz);
    }

    @Override
    public QuizDto getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        return mapToDto(quiz);
    }

    @Override
    public QuizDto getQuizDetailById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        QuizDto quizDto = mapToDto(quiz);

        // Ajouter les statistiques
        quizDto.setNbEvaluations(quizRepository.countEvaluationsByQuizId(id));
        quizDto.setMoyenneNotes(quizRepository.getAverageNoteByQuizId(id));

        // Calculer le taux de réussite
        Long totalCorrigees = quizRepository.countEvaluationsCorrigeesByQuizId(id);
        Long totalReussies = quizRepository.countEvaluationsReussiesByQuizId(id);
        if (totalCorrigees != null && totalCorrigees > 0) {
            quizDto.setTauxReussite((totalReussies.doubleValue() / totalCorrigees.doubleValue()) * 100.0);
        } else {
            quizDto.setTauxReussite(0.0);
        }

        return quizDto;
    }

    @Override
    public Page<QuizDto> getAllQuiz(Pageable pageable) {
        return quizRepository.findByActifTrueOrderByDateCreationDesc(pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<QuizDto> rechercherQuiz(String searchTerm, Pageable pageable) {
        return quizRepository.rechercherQuiz(searchTerm, pageable)
                .map(this::mapToDto);
    }

    @Override
    public List<QuizDto> getQuizByCreateurId(Long createurId) {
        return quizRepository.findByCreateurIdAndActifTrue(createurId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizDto> getQuizByFormationId(Long formationId) {
        return quizRepository.findByFormationIdAndActifTrue(formationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuizDto> getQuizByModuleId(Long moduleId) {
        return quizRepository.findByModuleIdAndActifTrue(moduleId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuizDto updateQuiz(Long id, QuizDto quizDto) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        // Mettre à jour les champs
        quiz.setTitre(quizDto.getTitre());
        quiz.setDescription(quizDto.getDescription());
        quiz.setDuree(quizDto.getDuree());
        quiz.setNoteMinimale(quizDto.getNoteMinimale());
        quiz.setNombreTentativesMax(quizDto.getNombreTentativesMax());
        quiz.setMelangQuestions(quizDto.isMelangQuestions());
        quiz.setMelangReponses(quizDto.isMelangReponses());
        quiz.setAffichageResultatImmediat(quizDto.isAffichageResultatImmediat());

        Quiz updatedQuiz = quizRepository.save(quiz);
        return mapToDto(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quiz", "id", id);
        }
        // TODO: Vérifier s'il y a des évaluations en cours
        quizRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void activerQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        quiz.setActif(true);
        quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public void desactiverQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));
        quiz.setActif(false);
        quizRepository.save(quiz);
    }

    @Override
    @Transactional
    public QuizDto duppliquerQuiz(Long id, Long nouveauCreateurId) {
        Quiz quizOriginal = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", id));

        // Créer une copie du quiz
        Quiz nouveauQuiz = Quiz.builder()
                .titre(quizOriginal.getTitre() + " (Copie)")
                .description(quizOriginal.getDescription())
                .formationId(quizOriginal.getFormationId())
                .moduleId(quizOriginal.getModuleId())
                .createurId(nouveauCreateurId)
                .duree(quizOriginal.getDuree())
                .noteMinimale(quizOriginal.getNoteMinimale())
                .nombreTentativesMax(quizOriginal.getNombreTentativesMax())
                .melangQuestions(quizOriginal.isMelangQuestions())
                .melangReponses(quizOriginal.isMelangReponses())
                .affichageResultatImmediat(quizOriginal.isAffichageResultatImmediat())
                .actif(false) // Désactivé par défaut
                .build();

        Quiz savedQuiz = quizRepository.save(nouveauQuiz);
        // TODO: Dupliquer les questions et choix de réponses

        return mapToDto(savedQuiz);
    }

    // Méthodes utilitaires de mapping
    private QuizDto mapToDto(Quiz quiz) {
        String createurNom = null;
        try {
            ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(quiz.getCreateurId());
            if (userResponse.getBody() != null) {
                UserDto user = userResponse.getBody();
                createurNom = user.getNom() + " " + user.getPrenom();
            }
        } catch (Exception e) {
            createurNom = "Utilisateur inconnu";
        }

        // Calculer le nombre de questions et points depuis le repository
        Integer nbQuestions = questionRepository.countByQuizId(quiz.getId());
        Integer totalPoints = questionRepository.calculateTotalPointsByQuizId(quiz.getId());

        return QuizDto.builder()
                .id(quiz.getId())
                .titre(quiz.getTitre())
                .description(quiz.getDescription())
                .formationId(quiz.getFormationId())
                .moduleId(quiz.getModuleId())
                .createurId(quiz.getCreateurId())
                .createurNom(createurNom)
                .duree(quiz.getDuree())
                .noteMinimale(quiz.getNoteMinimale())
                .nombreTentativesMax(quiz.getNombreTentativesMax())
                .melangQuestions(quiz.isMelangQuestions())
                .melangReponses(quiz.isMelangReponses())
                .affichageResultatImmediat(quiz.isAffichageResultatImmediat())
                .actif(quiz.isActif())
                .dateCreation(quiz.getDateCreation())
                .dateMiseAJour(quiz.getDateMiseAJour())
                .nbQuestions(nbQuestions != null ? nbQuestions : 0)
                .totalPoints(totalPoints != null ? totalPoints : 0)
                .build();
    }

    private Quiz mapToEntity(QuizDto quizDto) {
        return Quiz.builder()
                .titre(quizDto.getTitre())
                .description(quizDto.getDescription())
                .formationId(quizDto.getFormationId())
                .moduleId(quizDto.getModuleId())
                .createurId(quizDto.getCreateurId())
                .duree(quizDto.getDuree())
                .noteMinimale(quizDto.getNoteMinimale())
                .nombreTentativesMax(quizDto.getNombreTentativesMax())
                .melangQuestions(quizDto.isMelangQuestions())
                .melangReponses(quizDto.isMelangReponses())
                .affichageResultatImmediat(quizDto.isAffichageResultatImmediat())
                .build();
    }
}
