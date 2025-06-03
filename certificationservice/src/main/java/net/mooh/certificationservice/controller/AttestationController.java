package net.mooh.certificationservice.controller;

import lombok.RequiredArgsConstructor;
import net.mooh.certificationservice.dtos.AttestationDto;
import net.mooh.certificationservice.dtos.DemandeAttestationDto;
import net.mooh.certificationservice.dtos.ValidationAttestationDto;
import net.mooh.certificationservice.entities.Attestation.StatutAttestation;
import net.mooh.certificationservice.service.AttestationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/attestations")
@RequiredArgsConstructor
public class AttestationController {

    private final AttestationService attestationService;

    @PostMapping("/demander")
    public ResponseEntity<AttestationDto> demanderAttestation(@Valid @RequestBody DemandeAttestationDto demandeDto) {
        return new ResponseEntity<>(attestationService.demanderAttestation(demandeDto), HttpStatus.CREATED);
    }

    @PostMapping("/valider")
    public ResponseEntity<AttestationDto> validerAttestation(
            @Valid @RequestBody ValidationAttestationDto validationDto,
            @RequestHeader("User-Id") Long validateurId) {
        return ResponseEntity.ok(attestationService.validerAttestation(validationDto, validateurId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttestationDto> getAttestationById(@PathVariable Long id) {
        return ResponseEntity.ok(attestationService.getAttestationById(id));
    }

    @GetMapping("/numero/{numeroAttestation}")
    public ResponseEntity<AttestationDto> getAttestationByNumero(@PathVariable String numeroAttestation) {
        return ResponseEntity.ok(attestationService.getAttestationByNumero(numeroAttestation));
    }

    @GetMapping("/verifier/{codeVerification}")
    public ResponseEntity<AttestationDto> verifierAttestation(@PathVariable String codeVerification) {
        return ResponseEntity.ok(attestationService.verifierAttestation(codeVerification));
    }

    @GetMapping("/beneficiaire/{beneficiaireId}")
    public ResponseEntity<List<AttestationDto>> getAttestationsByBeneficiaire(@PathVariable Long beneficiaireId) {
        return ResponseEntity.ok(attestationService.getAttestationsByBeneficiaire(beneficiaireId));
    }

    @GetMapping("/certification/{certificationId}")
    public ResponseEntity<List<AttestationDto>> getAttestationsByCertification(@PathVariable Long certificationId) {
        return ResponseEntity.ok(attestationService.getAttestationsByCertification(certificationId));
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<Page<AttestationDto>> getAttestationsByStatut(
            @PathVariable StatutAttestation statut,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(attestationService.getAttestationsByStatut(statut, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttestationDto> updateAttestation(
            @PathVariable Long id,
            @Valid @RequestBody AttestationDto attestationDto) {
        return ResponseEntity.ok(attestationService.updateAttestation(id, attestationDto));
    }

    @PatchMapping("/{id}/revoquer")
    public ResponseEntity<Void> revoquerAttestation(
            @PathVariable Long id,
            @RequestParam String motif,
            @RequestHeader("User-Id") Long validateurId) {
        attestationService.revoquerAttestation(id, motif, validateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/suspendre")
    public ResponseEntity<Void> suspendreAttestation(
            @PathVariable Long id,
            @RequestParam String motif,
            @RequestHeader("User-Id") Long validateurId) {
        attestationService.suspendreAttestation(id, motif, validateurId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/reactiver")
    public ResponseEntity<Void> reactiverAttestation(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long validateurId) {
        attestationService.reactiverAttestation(id, validateurId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> genererPdfAttestation(@PathVariable Long id) {
        byte[] pdfContent = attestationService.genererPdfAttestation(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "attestation_" + id + ".pdf");

        attestationService.marquerCommeTelecharge(id);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfContent);
    }

    @GetMapping("/expirant-bientot")
    public ResponseEntity<List<AttestationDto>> getAttestationsExpirantBientot(
            @RequestParam(defaultValue = "30") int nombreJours) {
        return ResponseEntity.ok(attestationService.getAttestationsExpirantBientot(nombreJours));
    }

    @PostMapping("/traiter-expirees")
    public ResponseEntity<Void> traiterAttestationsExpirees() {
        attestationService.traiterAttestationsExpirees();
        return ResponseEntity.ok().build();
    }
}