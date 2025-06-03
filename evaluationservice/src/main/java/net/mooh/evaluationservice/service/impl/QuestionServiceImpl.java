package net.mooh.evaluationservice.service.impl;

import lombok.RequiredArgsConstructor;
import net.mooh.evaluationservice.dtos.ChoixReponseDto;
import net.mooh.evaluationservice.dtos.QuestionDto;
import net.mooh.evaluationservice.entities.ChoixReponse;
import net.mooh.evaluationservice.entities.Question;
import net.mooh.evaluationservice.entities.Quiz;
import net.mooh.evaluationservice.exception.ResourceNotFoundException;
import net.mooh.evaluationservice.repository.ChoixReponseRepository;
import net.mooh.evaluationservice.repository.QuestionRepository;
import net.mooh.evaluationservice.repository.QuizRepository;
import net.mooh.evaluationservice.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final ChoixReponseRepository choixReponseRepository;

    @Override
    @Transactional
    public QuestionDto creerQuestion(Long quizId, QuestionDto questionDto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        // Déterminer l'ordre de la nouvelle question
        Integer ordre = questionRepository.findMaxOrdreByQuizId(quizId);
        if (ordre == null) {
            ordre = 0;
        } else {
            ordre += 1;
        }

        Question question = mapToEntity(questionDto);
        question.setQuiz(quiz);
        question.setOrdre(ordre);

        Question savedQuestion = questionRepository.save(question);

        // Créer les choix de réponses si présents
        if (questionDto.getChoixReponses() != null && !questionDto.getChoixReponses().isEmpty()) {
            for (ChoixReponseDto choixDto : questionDto.getChoixReponses()) {
                ChoixReponse choix = ChoixReponse.builder()
                        .texte(choixDto.getTexte())
                        .correct(choixDto.isCorrect())
                        .ordre(choixDto.getOrdre())
                        .question(savedQuestion)
                        .build();
                choixReponseRepository.save(choix);
            }
        }

        return mapToDto(savedQuestion);
    }

    @Override
    public QuestionDto getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        return mapToDto(question);
    }

    @Override
    public List<QuestionDto> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizIdOrderByOrdre(quizId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public QuestionDto updateQuestion(Long id, QuestionDto questionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        question.setEnonce(questionDto.getEnonce());
        question.setType(questionDto.getType());
        question.setExplication(questionDto.getExplication());
        question.setPoints(questionDto.getPoints());
        question.setObligatoire(questionDto.isObligatoire());
        question.setReponseTexte(questionDto.getReponseTexte());

        Question updatedQuestion = questionRepository.save(question);
        return mapToDto(updatedQuestion);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question", "id", id);
        }
        questionRepository.deleteById(id);
        // TODO: Réorganiser les ordres des questions restantes
    }

    @Override
    @Transactional
    public void deplacerQuestion(Long id, Integer nouvelOrdre) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        Integer ordreActuel = question.getOrdre();
        Long quizId = question.getQuiz().getId();

        // Récupérer toutes les questions du quiz
        List<Question> questions = questionRepository.findByQuizIdOrderByOrdre(quizId);

        // Vérifier que le nouvel ordre est valide
        if (nouvelOrdre < 0 || nouvelOrdre >= questions.size()) {
            throw new IllegalArgumentException("Ordre invalide");
        }

        // Logique de déplacement similaire à celle des contenus
        if (ordreActuel < nouvelOrdre) {
            for (Question q : questions) {
                if (q.getOrdre() > ordreActuel && q.getOrdre() <= nouvelOrdre) {
                    q.setOrdre(q.getOrdre() - 1);
                    questionRepository.save(q);
                }
            }
        } else if (ordreActuel > nouvelOrdre) {
            for (Question q : questions) {
                if (q.getOrdre() >= nouvelOrdre && q.getOrdre() < ordreActuel) {
                    q.setOrdre(q.getOrdre() + 1);
                    questionRepository.save(q);
                }
            }
        }

        question.setOrdre(nouvelOrdre);
        questionRepository.save(question);
    }

    // Méthodes utilitaires de mapping
    private QuestionDto mapToDto(Question question) {
        List<ChoixReponseDto> choixReponses = choixReponseRepository.findByQuestionIdOrderByOrdre(question.getId())
                .stream()
                .map(this::mapChoixToDto)
                .collect(Collectors.toList());

        return QuestionDto.builder()
                .id(question.getId())
                .enonce(question.getEnonce())
                .type(question.getType())
                .explication(question.getExplication())
                .points(question.getPoints())
                .ordre(question.getOrdre())
                .obligatoire(question.isObligatoire())
                .quizId(question.getQuiz().getId())
                .choixReponses(choixReponses)
                .reponseTexte(question.getReponseTexte())
                .dateCreation(question.getDateCreation())
                .dateMiseAJour(question.getDateMiseAJour())
                .build();
    }

    private Question mapToEntity(QuestionDto questionDto) {
        return Question.builder()
                .enonce(questionDto.getEnonce())
                .type(questionDto.getType())
                .explication(questionDto.getExplication())
                .points(questionDto.getPoints())
                .obligatoire(questionDto.isObligatoire())
                .reponseTexte(questionDto.getReponseTexte())
                .build();
    }

    private ChoixReponseDto mapChoixToDto(ChoixReponse choix) {
        return ChoixReponseDto.builder()
                .id(choix.getId())
                .texte(choix.getTexte())
                .correct(choix.isCorrect())
                .ordre(choix.getOrdre())
                .questionId(choix.getQuestion().getId())
                .build();
    }
}