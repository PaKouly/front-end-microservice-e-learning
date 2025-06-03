package net.mooh.contentservice.controller;
import lombok.RequiredArgsConstructor;
import net.mooh.contentservice.dtos.ContenuDto;
import net.mooh.contentservice.entities.Contenu;
import net.mooh.contentservice.service.ContenuService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contenus")
@RequiredArgsConstructor
public class ContenuController {

    private final ContenuService contenuService;

    @PostMapping("/section/{sectionId}")
    public ResponseEntity<ContenuDto> creerContenu(
            @PathVariable Long sectionId,
            @RequestBody ContenuDto contenuDto) {
        return new ResponseEntity<>(contenuService.creerContenu(sectionId, contenuDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenuDto> getContenuById(@PathVariable Long id) {
        return ResponseEntity.ok(contenuService.getContenuById(id));
    }

    @GetMapping("/section/{sectionId}")
    public ResponseEntity<List<ContenuDto>> getContenusBySectionId(@PathVariable Long sectionId) {
        return ResponseEntity.ok(contenuService.getContenusBySectionId(sectionId));
    }

    @GetMapping("/type/{type}/formation/{formationId}")
    public ResponseEntity<List<ContenuDto>> getContenusByType(
            @PathVariable Contenu.TypeContenu type,
            @PathVariable Long formationId) {
        return ResponseEntity.ok(contenuService.getContenusByType(type, formationId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContenuDto> updateContenu(
            @PathVariable Long id,
            @RequestBody ContenuDto contenuDto) {
        return ResponseEntity.ok(contenuService.updateContenu(id, contenuDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContenu(@PathVariable Long id) {
        contenuService.deleteContenu(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deplacer")
    public ResponseEntity<Void> deplacerContenu(
            @PathVariable Long id,
            @RequestParam Integer nouvelOrdre) {
        contenuService.deplacerContenu(id, nouvelOrdre);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/dureeEstimee")
    public ResponseEntity<Integer> getDureeEstimee(@PathVariable Long id) {
        return ResponseEntity.ok(contenuService.getDureeEstimee(id));
    }
}
