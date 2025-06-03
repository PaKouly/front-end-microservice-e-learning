package net.mooh.contentservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.dtos.SectionFormationDto;
import net.mooh.contentservice.service.SectionFormationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionFormationController {
    private final SectionFormationService sectionService;

    @PostMapping("/formation/{formationId}")
    public ResponseEntity<SectionFormationDto> creerSection(
            @PathVariable Long formationId,
            @RequestBody SectionFormationDto sectionDto) {
        return new ResponseEntity<>(sectionService.creerSection(formationId, sectionDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionFormationDto> getSectionById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getSectionById(id));
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<SectionFormationDto>> getSectionsByFormationId(@PathVariable Long formationId) {
        return ResponseEntity.ok(sectionService.getSectionsByFormationId(formationId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectionFormationDto> updateSection(
            @PathVariable Long id,
            @RequestBody SectionFormationDto sectionDto) {
        return ResponseEntity.ok(sectionService.updateSection(id, sectionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deplacer")
    public ResponseEntity<Void> deplacerSection(
            @PathVariable Long id,
            @RequestParam Integer nouvelOrdre) {
        sectionService.deplacerSection(id, nouvelOrdre);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/nbContenus")
    public ResponseEntity<Integer> getNbContenus(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getNbContenus(id));
    }

    @GetMapping("/{id}/dureeEstimee")
    public ResponseEntity<Integer> getDureeEstimee(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getDureeEstimee(id));
    }
}
