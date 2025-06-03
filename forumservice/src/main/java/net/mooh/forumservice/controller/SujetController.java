package net.mooh.forumservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.dtos.SujetDto;
import net.mooh.forumservice.service.SujetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/sujets")
@RequiredArgsConstructor
public class SujetController {

    private final SujetService sujetService;

    @PostMapping
    public ResponseEntity<SujetDto> creerSujet(@Valid @RequestBody SujetDto sujetDto) {
        return new ResponseEntity<>(sujetService.creerSujet(sujetDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SujetDto> getSujetById(
            @PathVariable Long id,
            @RequestHeader(value = "User-Id", required = false) Long utilisateurId) {
        return ResponseEntity.ok(sujetService.getSujetById(id, utilisateurId));
    }

    @GetMapping("/forum/{forumId}")
    public ResponseEntity<Page<SujetDto>> getSujetsByForumId(
            @PathVariable Long forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sujetService.getSujetsByForumId(forumId, pageable));
    }

    @GetMapping("/recherche")
    public ResponseEntity<Page<SujetDto>> rechercherSujets(
            @RequestParam Long forumId,
            @RequestParam String term,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sujetService.rechercherSujets(forumId, term, pageable));
    }

    @GetMapping("/auteur/{auteurId}")
    public ResponseEntity<List<SujetDto>> getSujetsByAuteurId(@PathVariable Long auteurId) {
        return ResponseEntity.ok(sujetService.getSujetsByAuteurId(auteurId));
    }

    @GetMapping("/forum/{forumId}/resolus")
    public ResponseEntity<Page<SujetDto>> getSujetsResolus(
            @PathVariable Long forumId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sujetService.getSujetsResolus(forumId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SujetDto> updateSujet(
            @PathVariable Long id,
            @Valid @RequestBody SujetDto sujetDto,
            @RequestHeader("User-Id") Long utilisateurId) {
        return ResponseEntity.ok(sujetService.updateSujet(id, sujetDto, utilisateurId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSujet(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long utilisateurId) {
        sujetService.deleteSujet(id, utilisateurId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/resolu")
    public ResponseEntity<Void> marquerResolu(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long utilisateurId) {
        sujetService.marquerResolu(id, utilisateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/epingler")
    public ResponseEntity<Void> epingler(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long moderateurId) {
        sujetService.epingler(id, moderateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desepingler")
    public ResponseEntity<Void> desepingler(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long moderateurId) {
        sujetService.desepingler(id, moderateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/verrouiller")
    public ResponseEntity<Void> verrouiller(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long moderateurId) {
        sujetService.verrouiller(id, moderateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deverrouiller")
    public ResponseEntity<Void> deverrouiller(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long moderateurId) {
        sujetService.deverrouiller(id, moderateurId);
        return ResponseEntity.ok().build();
    }
}
