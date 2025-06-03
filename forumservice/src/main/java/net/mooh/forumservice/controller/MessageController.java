package net.mooh.forumservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.dtos.MessageDto;
import net.mooh.forumservice.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDto> creerMessage(@Valid @RequestBody MessageDto messageDto) {
        return new ResponseEntity<>(messageService.creerMessage(messageDto), HttpStatus.CREATED);
    }

    @PostMapping("/{messageParentId}/reponses")
    public ResponseEntity<MessageDto> repondreMessage(
            @PathVariable Long messageParentId,
            @Valid @RequestBody MessageDto messageDto) {
        return new ResponseEntity<>(messageService.repondreMessage(messageParentId, messageDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> getMessageById(
            @PathVariable Long id,
            @RequestHeader(value = "User-Id", required = false) Long utilisateurId) {
        return ResponseEntity.ok(messageService.getMessageById(id, utilisateurId));
    }

    @GetMapping("/sujet/{sujetId}")
    public ResponseEntity<Page<MessageDto>> getMessagesBySujetId(
            @PathVariable Long sujetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestHeader(value = "User-Id", required = false) Long utilisateurId) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(messageService.getMessagesBySujetId(sujetId, pageable, utilisateurId));
    }

    @GetMapping("/auteur/{auteurId}")
    public ResponseEntity<List<MessageDto>> getMessagesByAuteurId(@PathVariable Long auteurId) {
        return ResponseEntity.ok(messageService.getMessagesByAuteurId(auteurId));
    }

    @GetMapping("/sujet/{sujetId}/non-valides")
    public ResponseEntity<Page<MessageDto>> getMessagesNonValides(
            @PathVariable Long sujetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(messageService.getMessagesNonValides(sujetId, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> updateMessage(
            @PathVariable Long id,
            @Valid @RequestBody MessageDto messageDto,
            @RequestHeader("User-Id") Long utilisateurId) {
        return ResponseEntity.ok(messageService.updateMessage(id, messageDto, utilisateurId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long utilisateurId) {
        messageService.deleteMessage(id, utilisateurId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/valider")
    public ResponseEntity<Void> validerMessage(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long moderateurId) {
        messageService.validerMessage(id, moderateurId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long utilisateurId) {
        messageService.like(id, utilisateurId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlike(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long utilisateurId) {
        messageService.unlike(id, utilisateurId);
        return ResponseEntity.ok().build();
    }
}
