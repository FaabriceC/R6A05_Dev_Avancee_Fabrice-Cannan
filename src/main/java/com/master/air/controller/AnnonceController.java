package com.master.air.controller;

import com.master.air.dto.*;
import com.master.air.model.AnnonceStatus;
import com.master.air.service.AnnonceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/annonces")
@Tag(name = "Annonces", description = "API CRUD Annonces")
public class AnnonceController {

    private final AnnonceService service;
    public AnnonceController(AnnonceService s) { this.service = s; }

    private Long userId(Authentication auth) { return (Long) auth.getDetails(); }

    @GetMapping
    @Operation(summary = "Liste paginee avec filtres Specifications (q, status, categoryId, authorId, fromDate, toDate)")
    public ResponseEntity<Page<AnnonceDTO>> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) LocalDateTime fromDate,
            @RequestParam(required = false) LocalDateTime toDate,
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {
        AnnonceStatus st = (status != null && !status.isBlank()) ? AnnonceStatus.valueOf(status) : null;
        return ResponseEntity.ok(service.search(q, st, categoryId, authorId, fromDate, toDate, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une annonce")
    public ResponseEntity<AnnonceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Creation (statut DRAFT)")
    public ResponseEntity<AnnonceDTO> create(@Valid @RequestBody AnnonceCreateDTO dto, Authentication auth) {
        AnnonceDTO created = service.create(dto, userId(auth));
        return ResponseEntity.created(URI.create("/api/annonces/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mise a jour complete")
    public ResponseEntity<AnnonceDTO> update(@PathVariable Long id, @Valid @RequestBody AnnonceCreateDTO dto, Authentication auth) {
        return ResponseEntity.ok(service.update(id, dto, userId(auth)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mise a jour partielle (PATCH bonus)")
    public ResponseEntity<AnnonceDTO> patch(@PathVariable Long id, @RequestBody AnnoncePatchDTO dto, Authentication auth) {
        return ResponseEntity.ok(service.patch(id, dto, userId(auth)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Suppression (doit etre ARCHIVED)")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, userId(auth));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publier DRAFT -> PUBLISHED")
    public ResponseEntity<AnnonceDTO> publish(@PathVariable Long id) {
        return ResponseEntity.ok(service.publish(id));
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archiver (ROLE_ADMIN requis via @PreAuthorize)")
    public ResponseEntity<AnnonceDTO> archive(@PathVariable Long id) {
        return ResponseEntity.ok(service.archive(id));
    }
}
