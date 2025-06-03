package net.mooh.certificationservice.service;

import net.mooh.certificationservice.dtos.CertificationDto;
import net.mooh.certificationservice.entities.Certification.TypeCertification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CertificationService {

    CertificationDto creerCertification(CertificationDto certificationDto);

    CertificationDto getCertificationById(Long id);

    CertificationDto getCertificationDetailById(Long id);

    Page<CertificationDto> getAllCertifications(Pageable pageable);

    Page<CertificationDto> rechercherCertifications(String searchTerm, Pageable pageable);

    List<CertificationDto> getCertificationsByCreateurId(Long createurId);

    List<CertificationDto> getCertificationsByFormationId(Long formationId);

    List<CertificationDto> getCertificationsByType(TypeCertification type);

    CertificationDto updateCertification(Long id, CertificationDto certificationDto);

    void deleteCertification(Long id);

    void activerCertification(Long id);

    void desactiverCertification(Long id);

    void ajouterFormationPrerequise(Long certificationId, Long formationId);

    void supprimerFormationPrerequise(Long certificationId, Long formationId);

    void ajouterQuizRequis(Long certificationId, Long quizId);

    void supprimerQuizRequis(Long certificationId, Long quizId);

    void ajouterCompetence(Long certificationId, String competence);

    void supprimerCompetence(Long certificationId, String competence);

    boolean peutObtenirCertification(Long beneficiaireId, Long certificationId);
}