package net.mooh.contentservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.dtos.FormationDto;
import net.mooh.contentservice.service.FormationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;

    @PostMapping
    public ResponseEntity<FormationDto> creerFormation(@RequestBody FormationDto formationDto) {
        return new ResponseEntity<>(formationService.creerFormation(formationDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormationDto> getFormationById(@PathVariable Long id) {
        return ResponseEntity.ok(formationService.getFormationById(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<FormationDto> getFormationDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(formationService.getFormationDetailById(id));
    }

    @GetMapping
    public ResponseEntity<Page<FormationDto>> getAllFormations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(formationService.getAllFormations(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<FormationDto>> rechercherFormations(
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(formationService.rechercherFormations(term, pageable));
    }

    @GetMapping("/createur/{createurId}")
    public ResponseEntity<List<FormationDto>> getFormationsByCreateurId(@PathVariable Long createurId) {
        return ResponseEntity.ok(formationService.getFormationsByCreateurId(createurId));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<FormationDto>> getFormationsByTag(@PathVariable String tag) {
        return ResponseEntity.ok(formationService.getFormationsByTag(tag));
    }

    @GetMapping("/niveau/{niveau}")
    public ResponseEntity<List<FormationDto>> getFormationsByNiveau(@PathVariable String niveau) {
        return ResponseEntity.ok(formationService.getFormationsByNiveau(niveau));
    }

    @GetMapping("/langue/{langue}")
    public ResponseEntity<List<FormationDto>> getFormationsByLangue(@PathVariable String langue) {
        return ResponseEntity.ok(formationService.getFormationsByLangue(langue));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormationDto> updateFormation(
            @PathVariable Long id,
            @RequestBody FormationDto formationDto) {
        return ResponseEntity.ok(formationService.updateFormation(id, formationDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable Long id) {
        formationService.deleteFormation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activerFormation(@PathVariable Long id) {
        formationService.activerFormation(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiverFormation(@PathVariable Long id) {
        formationService.desactiverFormation(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/tags")
    public ResponseEntity<Void> ajouterTag(
            @PathVariable Long id,
            @RequestParam String tag) {
        formationService.ajouterTag(id, tag);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/tags")
    public ResponseEntity<Void> supprimerTag(
            @PathVariable Long id,
            @RequestParam String tag) {
        formationService.supprimerTag(id, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/prerequis")
    public ResponseEntity<Void> ajouterPrerequis(
            @PathVariable Long id,
            @RequestParam String prerequis) {
        formationService.ajouterPrerequis(id, prerequis);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/prerequis")
    public ResponseEntity<Void> supprimerPrerequis(
            @PathVariable Long id,
            @RequestParam String prerequis) {
        formationService.supprimerPrerequis(id, prerequis);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/objectifs")
    public ResponseEntity<Void> ajouterObjectif(
            @PathVariable Long id,
            @RequestParam String objectif) {
        formationService.ajouterObjectif(id, objectif);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/objectifs")
    public ResponseEntity<Void> supprimerObjectif(
            @PathVariable Long id,
            @RequestParam String objectif) {
        formationService.supprimerObjectif(id, objectif);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tags")
    public ResponseEntity<List<String>> getAllTags() {
        return ResponseEntity.ok(formationService.getAllTags());
    }

    @GetMapping("/niveaux")
    public ResponseEntity<List<String>> getAllNiveaux() {
        return ResponseEntity.ok(formationService.getAllNiveaux());
    }

    @GetMapping("/langues")
    public ResponseEntity<List<String>> getAllLangues() {
        return ResponseEntity.ok(formationService.getAllLangues());
    }
}
