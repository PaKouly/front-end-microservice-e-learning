package net.mooh.certificationservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.certificationservice.dtos.CertificationDto;
import net.mooh.certificationservice.entities.Certification.TypeCertification;
import net.mooh.certificationservice.service.CertificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    public ResponseEntity<CertificationDto> creerCertification(@Valid @RequestBody CertificationDto certificationDto) {
        return new ResponseEntity<>(certificationService.creerCertification(certificationDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificationDto> getCertificationById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationService.getCertificationById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<CertificationDto> getCertificationDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(certificationService.getCertificationDetailById(id));
    }

    @GetMapping
    public ResponseEntity<Page<CertificationDto>> getAllCertifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(certificationService.getAllCertifications(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<CertificationDto>> rechercherCertifications(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(certificationService.rechercherCertifications(term, pageable));
    }

    @GetMapping("/createur/{createurId}")
    public ResponseEntity<List<CertificationDto>> getCertificationsByCreateurId(@PathVariable Long createurId) {
        return ResponseEntity.ok(certificationService.getCertificationsByCreateurId(createurId));
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<CertificationDto>> getCertificationsByFormationId(@PathVariable Long formationId) {
        return ResponseEntity.ok(certificationService.getCertificationsByFormationId(formationId));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CertificationDto>> getCertificationsByType(@PathVariable TypeCertification type) {
        return ResponseEntity.ok(certificationService.getCertificationsByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificationDto> updateCertification(
            @PathVariable Long id,
            @Valid @RequestBody CertificationDto certificationDto) {
        return ResponseEntity.ok(certificationService.updateCertification(id, certificationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertification(@PathVariable Long id) {
        certificationService.deleteCertification(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activerCertification(@PathVariable Long id) {
        certificationService.activerCertification(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiverCertification(@PathVariable Long id) {
        certificationService.desactiverCertification(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/formations-prerequises")
    public ResponseEntity<Void> ajouterFormationPrerequise(
            @PathVariable Long id,
            @RequestParam Long formationId) {
        certificationService.ajouterFormationPrerequise(id, formationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/formations-prerequises")
    public ResponseEntity<Void> supprimerFormationPrerequise(
            @PathVariable Long id,
            @RequestParam Long formationId) {
        certificationService.supprimerFormationPrerequise(id, formationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/quiz-requis")
    public ResponseEntity<Void> ajouterQuizRequis(
            @PathVariable Long id,
            @RequestParam Long quizId) {
        certificationService.ajouterQuizRequis(id, quizId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/quiz-requis")
    public ResponseEntity<Void> supprimerQuizRequis(
            @PathVariable Long id,
            @RequestParam Long quizId) {
        certificationService.supprimerQuizRequis(id, quizId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/competences")
    public ResponseEntity<Void> ajouterCompetence(
            @PathVariable Long id,
            @RequestParam String competence) {
        certificationService.ajouterCompetence(id, competence);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/competences")
    public ResponseEntity<Void> supprimerCompetence(
            @PathVariable Long id,
            @RequestParam String competence) {
        certificationService.supprimerCompetence(id, competence);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/peut-obtenir")
    public ResponseEntity<Boolean> peutObtenirCertification(
            @RequestParam Long beneficiaireId,
            @RequestParam Long certificationId) {
        return ResponseEntity.ok(certificationService.peutObtenirCertification(beneficiaireId, certificationId));
    }
}