package net.mooh.forumservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.dtos.SignalementDto;
import net.mooh.forumservice.entities.Signalement.StatutSignalement;
import net.mooh.forumservice.service.SignalementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/signalements")
@RequiredArgsConstructor
public class SignalementController {

    private final SignalementService signalementService;

    @PostMapping
    public ResponseEntity<SignalementDto> creerSignalement(@Valid @RequestBody SignalementDto signalementDto) {
        return new ResponseEntity<>(signalementService.creerSignalement(signalementDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SignalementDto> getSignalementById(@PathVariable Long id) {
        return ResponseEntity.ok(signalementService.getSignalementById(id));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<Page<SignalementDto>> getSignalementsByStatut(
            @PathVariable StatutSignalement statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(signalementService.getSignalementsByStatut(statut, pageable));
    }

    @GetMapping("/forum/{forumId}/statut/{statut}")
    public ResponseEntity<Page<SignalementDto>> getSignalementsByForumIdAndStatut(
            @PathVariable Long forumId,
            @PathVariable StatutSignalement statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(signalementService.getSignalementsByForumIdAndStatut(forumId, statut, pageable));
    }

    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<SignalementDto>> getSignalementsByMessageId(@PathVariable Long messageId) {
        return ResponseEntity.ok(signalementService.getSignalementsByMessageId(messageId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SignalementDto> updateSignalement(
            @PathVariable Long id,
            @Valid @RequestBody SignalementDto signalementDto,
            @RequestHeader("User-Id") Long moderateurId) {
        return ResponseEntity.ok(signalementService.updateSignalement(id, signalementDto, moderateurId));
    }

    @PatchMapping("/{id}/en-cours")
    public ResponseEntity<Void> marquerEnCours(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long moderateurId) {
        signalementService.marquerEnCours(id, moderateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/resolu")
    public ResponseEntity<Void> marquerResolu(
            @PathVariable Long id,
            @RequestParam String commentaire,
            @RequestHeader("User-Id") Long moderateurId) {
        signalementService.marquerResolu(id, commentaire, moderateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/rejete")
    public ResponseEntity<Void> marquerRejete(
            @PathVariable Long id,
            @RequestParam String commentaire,
            @RequestHeader("User-Id") Long moderateurId) {
        signalementService.marquerRejete(id, commentaire, moderateurId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/forum/{forumId}/non-resolus/count")
    public ResponseEntity<Integer> countSignalementsNonResolus(@PathVariable Long forumId) {
        return ResponseEntity.ok(signalementService.countSignalementsNonResolus(forumId));
    }
}
