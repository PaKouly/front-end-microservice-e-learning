package net.mooh.forumservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.forumservice.dtos.ForumDto;
import net.mooh.forumservice.service.ForumService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/forums")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    @PostMapping
    public ResponseEntity<ForumDto> creerForum(@Valid @RequestBody ForumDto forumDto) {
        return new ResponseEntity<>(forumService.creerForum(forumDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ForumDto> getForumById(@PathVariable Long id) {
        return ResponseEntity.ok(forumService.getForumById(id));
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<ForumDto> getForumByFormationId(@PathVariable Long formationId) {
        return ResponseEntity.ok(forumService.getForumByFormationId(formationId));
    }

    @GetMapping
    public ResponseEntity<List<ForumDto>> getAllForums() {
        return ResponseEntity.ok(forumService.getAllForums());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ForumDto> updateForum(
            @PathVariable Long id,
            @Valid @RequestBody ForumDto forumDto) {
        return ResponseEntity.ok(forumService.updateForum(id, forumDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForum(@PathVariable Long id) {
        forumService.deleteForum(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activer")
    public ResponseEntity<Void> activerForum(@PathVariable Long id) {
        forumService.activerForum(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/desactiver")
    public ResponseEntity<Void> desactiverForum(@PathVariable Long id) {
        forumService.desactiverForum(id);
        return ResponseEntity.ok().build();
    }
}
