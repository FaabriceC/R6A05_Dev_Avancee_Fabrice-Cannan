package com.master.air.controller;

import com.master.air.dto.AnnonceCreateRequestDTO;
import com.master.air.dto.AnnonceDTO;
import com.master.air.dto.AnnonceUpdateRequestDTO;
import com.master.air.model.AnnonceStatus;
import com.master.air.service.AnnonceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    private final AnnonceService annonceService;

    public AnnonceController(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    // Exo3: liste paginée + tri + filtres
    // Exemples:
    // /api/annonces?page=0&size=10&sort=date,desc
    // /api/annonces?q=paris&status=DRAFT&categoryId=1&authorId=2
    // /api/annonces?fromDate=1700000000000&toDate=1900000000000
    @GetMapping
    public Page<AnnonceDTO> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) AnnonceStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Long fromDate,
            @RequestParam(required = false) Long toDate,
            Pageable pageable
    ) {
        return annonceService.search(q, status, categoryId, authorId, fromDate, toDate, pageable);
    }

    @GetMapping("/{id}")
    public AnnonceDTO getById(@PathVariable Long id) {
        return annonceService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AnnonceDTO create(@Valid @RequestBody AnnonceCreateRequestDTO dto) {
        return annonceService.create(dto);
    }

    @PutMapping("/{id}")
    public AnnonceDTO update(@PathVariable Long id, @Valid @RequestBody AnnonceUpdateRequestDTO dto) {
        return annonceService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        annonceService.delete(id);
    }
}