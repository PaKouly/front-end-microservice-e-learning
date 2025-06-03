package net.mooh.evaluationservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.evaluationservice.client.UserClient;
import net.mooh.evaluationservice.client.UserDto;
import net.mooh.evaluationservice.dtos.EvaluationDto;
import net.mooh.evaluationservice.dtos.PassageQuizDto;
import net.mooh.evaluationservice.dtos.ReponseApprenantDto;
import net.mooh.evaluationservice.dtos.SoumissionReponseDto;
import net.mooh.evaluationservice.entities.*;
import net.mooh.evaluationservice.entities.Evaluation.StatutEvaluation;
import net.mooh.evaluationservice.exception.ResourceNotFoundException;
import net.mooh.evaluationservice.exception.UnauthorizedException;
import net.mooh.evaluationservice.repository.*;
import net.mooh.evaluationservice.service.EvaluationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ChoixReponseRepository choixReponseRepository;
    private final ReponseApprenantRepository reponseApprenantRepository;
    @Qualifier("net.mooh.evaluationservice.client.UserClient")
    private final UserClient userClient;

    @Override
    @Transactional
    public EvaluationDto demarrerQuiz(PassageQuizDto passageQuizDto) {
        // Vérifier que l'utilisateur existe et a le rôle APPRENANT
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(passageQuizDto.getApprenantId());
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", passageQuizDto.getApprenantId());
        }

        UserDto user = userResponse.getBody();
        if (!user.getRoles().contains("APPRENANT")) {
            throw new UnauthorizedException("L'utilisateur n'a pas le rôle APPRENANT");
        }

        // Vérifier que le quiz existe et est actif
        Quiz quiz = quizRepository.findById(passageQuizDto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", passageQuizDto.getQuizId()));

        if (!quiz.isActif()) {
            throw new UnauthorizedException("Ce quiz n'est pas actif");
        }

        // Vérifier si l'apprenant peut passer le quiz
        if (!peutPasserQuiz(passageQuizDto.getApprenantId(), passageQuizDto.getQuizId())) {
            throw new UnauthorizedException("Nombre maximum de tentatives atteint");
        }

        // Vérifier s'il y a déjà une évaluation en cours
        if (evaluationRepository.findByApprenantIdAndQuizIdAndStatut(
                passageQuizDto.getApprenantId(),
                passageQuizDto.getQuizId(),
                StatutEvaluation.EN_COURS).isPresent()) {
            throw new UnauthorizedException("Une évaluation est déjà en cours pour ce quiz");
        }

        // Créer la nouvelle évaluation
        Integer numeroTentative = getNbTentatives(passageQuizDto.getApprenantId(), passageQuizDto.getQuizId()) + 1;

        Evaluation evaluation = Evaluation.builder()
                .quiz(quiz)
                .apprenantId(passageQuizDto.getApprenantId())
                .apprenantNom(user.getNom() + " " + user.getPrenom())
                .tentative(numeroTentative)
                .statut(StatutEvaluation.EN_COURS)
                .dateDebut(LocalDateTime.now())
                .noteMaximale(quiz.getTotalPoints().doubleValue())
                .reussie(false)
                .build();

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return mapToDto(savedEvaluation);
    }

    @Override
    @Transactional
    public EvaluationDto soumettreReponses(SoumissionReponseDto soumissionDto) {
        Evaluation evaluation = evaluationRepository.findById(soumissionDto.getEvaluationId())
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation", "id", soumissionDto.getEvaluationId()));

        if (evaluation.getStatut() != StatutEvaluation.EN_COURS) {
            throw new UnauthorizedException("Cette évaluation n'est plus en cours");
        }

        // Enregistrer ou mettre à jour les réponses
        for (ReponseApprenantDto reponseDto : soumissionDto.getReponses()) {
            ReponseApprenant reponse = reponseApprenantRepository
                    .findByEvaluationIdAndQuestionId(evaluation.getId(), reponseDto.getQuestionId())
                    .orElse(new ReponseApprenant());

            Question question = questionRepository.findById(reponseDto.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question", "id", reponseDto.getQuestionId()));

            reponse.setEvaluation(evaluation);
            reponse.setQuestion(question);
            reponse.setChoixSelectionnes(reponseDto.getChoixSelectionnes());
            reponse.setReponseTexte(reponseDto.getReponseTexte());
            reponse.setReponseNumerique(reponseDto.getReponseNumerique());

            reponseApprenantRepository.save(reponse);
        }

        return mapToDto(evaluation);
    }

    @Override
    @Transactional
    public EvaluationDto terminerQuiz(Long evaluationId) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation", "id", evaluationId));

        if (evaluation.getStatut() != StatutEvaluation.EN_COURS) {
            throw new UnauthorizedException("Cette évaluation n'est plus en cours");
        }

        evaluation.setDateFin(LocalDateTime.now());
        evaluation.setDureeReelle((int) ChronoUnit.SECONDS.between(evaluation.getDateDebut(), evaluation.getDateFin()));
        evaluation.setStatut(StatutEvaluation.TERMINEE);

        // Correction automatique pour les questions à choix multiple
        corrigerAutomatiquement(evaluation);

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return mapToDto(savedEvaluation);
    }

    @Override
    public EvaluationDto getEvaluationById(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation", "id", id));
        return mapToDto(evaluation);
    }

    @Override
    public List<EvaluationDto> getEvaluationsByApprenantId(Long apprenantId) {
        return evaluationRepository.findByApprenantIdOrderByDateCreationDesc(apprenantId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EvaluationDto> getEvaluationsByQuizId(Long quizId) {
        return evaluationRepository.findByQuizIdOrderByDateCreationDesc(quizId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EvaluationDto> getEvaluationsACorreiger(Pageable pageable) {
        return evaluationRepository.findEvaluationsACorreiger(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public EvaluationDto corrigerEvaluation(Long id, Long correcteurId) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation", "id", id));

        // Vérifier que le correcteur a les droits
        ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(correcteurId);
        if (userResponse.getBody() == null) {
            throw new ResourceNotFoundException("Utilisateur", "id", correcteurId);
        }

        UserDto user = userResponse.getBody();
        if (!user.getRoles().contains("FORMATEUR") && !user.getRoles().contains("ADMINISTRATEUR")) {
            throw new UnauthorizedException("L'utilisateur n'a pas le rôle nécessaire pour corriger");
        }

        evaluation.setCorrecteurId(correcteurId);
        evaluation.setStatut(StatutEvaluation.CORRIGEE);

        // Calculer la note finale
        Double totalPoints = reponseApprenantRepository.calculateTotalPointsByEvaluationId(id);
        evaluation.setNoteObtenue(totalPoints != null ? totalPoints : 0.0);
        evaluation.calculerPourcentage();
        evaluation.verifierReussite();

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return mapToDto(savedEvaluation);
    }

    @Override
    @Transactional
    public EvaluationDto ajouterCommentaire(Long id, String commentaire, Long correcteurId) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluation", "id", id));

        evaluation.setCommentaire(commentaire);
        evaluation.setCorrecteurId(correcteurId);

        Evaluation savedEvaluation = evaluationRepository.save(evaluation);
        return mapToDto(savedEvaluation);
    }

    @Override
    public Integer getNbTentatives(Long apprenantId, Long quizId) {
        return evaluationRepository.countByApprenantIdAndQuizId(apprenantId, quizId);
    }

    @Override
    public boolean peutPasserQuiz(Long apprenantId, Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        if (quiz.getNombreTentativesMax() == null) {
            return true; // Pas de limite
        }

        Integer nbTentatives = getNbTentatives(apprenantId, quizId);
        return nbTentatives < quiz.getNombreTentativesMax();
    }

    // Méthode privée pour la correction automatique
    private void corrigerAutomatiquement(Evaluation evaluation) {
        List<ReponseApprenant> reponses = reponseApprenantRepository.findByEvaluationId(evaluation.getId());

        for (ReponseApprenant reponse : reponses) {
            Question question = reponse.getQuestion();

            switch (question.getType()) {
                case QCM_UNIQUE:
                case QCM_MULTIPLE:
                case VRAI_FAUX:
                    corrigerQCM(reponse);
                    break;
                case NUMERIQUE:
                    corrigerNumerique(reponse);
                    break;
                case TEXTE_LIBRE:
                case CORRESPONDANCE:
                case ORDONNANCEMENT:
                    // Correction manuelle requise
                    reponse.setPointsObtenus(0.0);
                    break;
            }

            reponseApprenantRepository.save(reponse);
        }
    }

    private void corrigerQCM(ReponseApprenant reponse) {
        List<ChoixReponse> bonnesReponses = choixReponseRepository
                .findCorrectChoicesByQuestionId(reponse.getQuestion().getId());

        boolean correct = false;

        if (reponse.getQuestion().getType() == Question.TypeQuestion.QCM_UNIQUE) {
            // Pour QCM unique : une seule bonne réponse
            if (reponse.getChoixSelectionnes().size() == 1 && bonnesReponses.size() == 1) {
                correct = reponse.getChoixSelectionnes().contains(bonnesReponses.get(0).getId());
            }
        } else {
            // Pour QCM multiple : toutes les bonnes réponses doivent être sélectionnées
            if (reponse.getChoixSelectionnes().size() == bonnesReponses.size()) {
                correct = bonnesReponses.stream()
                        .allMatch(choix -> reponse.getChoixSelectionnes().contains(choix.getId()));
            }
        }

        reponse.setCorrecte(correct);
        reponse.setPointsObtenus(correct ? reponse.getQuestion().getPoints().doubleValue() : 0.0);
    }

    private void corrigerNumerique(ReponseApprenant reponse) {
        // Logique de correction pour les réponses numériques
        // À implémenter selon les besoins (tolérance, etc.)
        reponse.setPointsObtenus(0.0); // Placeholder
    }

    // Méthode utilitaire de mapping
    private EvaluationDto mapToDto(Evaluation evaluation) {
        String correcteurNom = null;
        if (evaluation.getCorrecteurId() != null) {
            try {
                ResponseEntity<UserDto> userResponse = userClient.getUtilisateurById(evaluation.getCorrecteurId());
                if (userResponse.getBody() != null) {
                    UserDto user = userResponse.getBody();
                    correcteurNom = user.getNom() + " " + user.getPrenom();
                }
            } catch (Exception e) {
                correcteurNom = "Correcteur inconnu";
            }
        }

        return EvaluationDto.builder()
                .id(evaluation.getId())
                .quizId(evaluation.getQuiz().getId())
                .quizTitre(evaluation.getQuiz().getTitre())
                .apprenantId(evaluation.getApprenantId())
                .apprenantNom(evaluation.getApprenantNom())
                .tentative(evaluation.getTentative())
                .statut(evaluation.getStatut())
                .noteObtenue(evaluation.getNoteObtenue())
                .noteMaximale(evaluation.getNoteMaximale())
                .pourcentage(evaluation.getPourcentage())
                .dateDebut(evaluation.getDateDebut())
                .dateFin(evaluation.getDateFin())
                .dureeReelle(evaluation.getDureeReelle())
                .reussie(evaluation.isReussie())
                .commentaire(evaluation.getCommentaire())
                .correcteurId(evaluation.getCorrecteurId())
                .correcteurNom(correcteurNom)
                .dateCreation(evaluation.getDateCreation())
                .dateMiseAJour(evaluation.getDateMiseAJour())
                .build();
    }
}