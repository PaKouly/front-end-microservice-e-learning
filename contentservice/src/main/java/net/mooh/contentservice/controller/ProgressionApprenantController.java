package net.mooh.contentservice.controller;
import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.dtos.ProgressionApprenantDto;
import net.mooh.contentservice.service.ProgressionApprenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/progressions")
@RequiredArgsConstructor
public class ProgressionApprenantController {

    private final ProgressionApprenantService progressionService;

    @PostMapping("/apprenant/{apprenantId}/formation/{formationId}")
    public ResponseEntity<ProgressionApprenantDto> inscrireApprenant(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        return new ResponseEntity<>(progressionService.inscrireApprenant(apprenantId, formationId), HttpStatus.CREATED);
    }

    @GetMapping("/apprenant/{apprenantId}/formation/{formationId}")
    public ResponseEntity<ProgressionApprenantDto> getProgressionByApprenantIdAndFormationId(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        return ResponseEntity.ok(progressionService.getProgressionByApprenantIdAndFormationId(apprenantId, formationId));
    }

    @GetMapping("/apprenant/{apprenantId}")
    public ResponseEntity<List<ProgressionApprenantDto>> getProgressionsByApprenantId(@PathVariable Long apprenantId) {
        return ResponseEntity.ok(progressionService.getProgressionsByApprenantId(apprenantId));
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<ProgressionApprenantDto>> getProgressionsByFormationId(@PathVariable Long formationId) {
        return ResponseEntity.ok(progressionService.getProgressionsByFormationId(formationId));
    }

    @PatchMapping("/apprenant/{apprenantId}/formation/{formationId}/contenu/{contenuId}")
    public ResponseEntity<ProgressionApprenantDto> marquerContenuComplet(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId,
            @PathVariable Long contenuId) {
        return ResponseEntity.ok(progressionService.marquerContenuComplet(apprenantId, formationId, contenuId));
    }

    @PatchMapping("/apprenant/{apprenantId}/formation/{formationId}/section/{sectionId}")
    public ResponseEntity<ProgressionApprenantDto> marquerSectionCompletee(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId,
            @PathVariable Long sectionId) {
        return ResponseEntity.ok(progressionService.marquerSectionCompletee(apprenantId, formationId, sectionId));
    }

    @PatchMapping("/apprenant/{apprenantId}/formation/{formationId}/consultation")
    public ResponseEntity<ProgressionApprenantDto> marquerDerniereConsultation(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        return ResponseEntity.ok(progressionService.marquerDerniereConsultation(apprenantId, formationId));
    }

    @PatchMapping("/apprenant/{apprenantId}/formation/{formationId}/calculer")
    public ResponseEntity<ProgressionApprenantDto> calculerProgression(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        return ResponseEntity.ok(progressionService.calculerProgression(apprenantId, formationId));
    }

    @DeleteMapping("/apprenant/{apprenantId}/formation/{formationId}")
    public ResponseEntity<Void> desinscrireApprenant(
            @PathVariable Long apprenantId,
            @PathVariable Long formationId) {
        progressionService.desinscrireApprenant(apprenantId, formationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/formation/{formationId}/tauxCompletion")
    public ResponseEntity<Double> getTauxCompletionFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(progressionService.getTauxCompletionFormation(formationId));
    }

    @GetMapping("/formation/{formationId}/nbInscrits")
    public ResponseEntity<Integer> getNbInscritsFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(progressionService.getNbInscritsFormation(formationId));
    }
}
