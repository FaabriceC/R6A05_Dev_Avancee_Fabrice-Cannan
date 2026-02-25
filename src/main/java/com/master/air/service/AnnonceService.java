package com.master.air.service;

import com.master.air.dto.*;
import com.master.air.exception.*;
import com.master.air.mapper.AnnonceMapper;
import com.master.air.model.*;
import com.master.air.repository.*;
import com.master.air.specification.AnnonceSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class AnnonceService {

    private final AnnonceRepository annonceRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;
    private final AnnonceMapper mapper;

    public AnnonceService(AnnonceRepository ar, UserRepository ur, CategoryRepository cr, AnnonceMapper m) {
        this.annonceRepo = ar; this.userRepo = ur; this.categoryRepo = cr; this.mapper = m;
    }

    /** Recherche multi-criteres via Specifications (Exercice 3) */
    public Page<AnnonceDTO> search(String keyword, AnnonceStatus status, Long categoryId,
                                    Long authorId, LocalDateTime fromDate, LocalDateTime toDate,
                                    Pageable pageable) {
        Specification<Annonce> spec = Specification.where(AnnonceSpecifications.fetchRelations());
        if (keyword != null && !keyword.isBlank()) spec = spec.and(AnnonceSpecifications.hasKeyword(keyword));
        if (status != null) spec = spec.and(AnnonceSpecifications.hasStatus(status));
        if (categoryId != null) spec = spec.and(AnnonceSpecifications.hasCategoryId(categoryId));
        if (authorId != null) spec = spec.and(AnnonceSpecifications.hasAuthorId(authorId));
        if (fromDate != null) spec = spec.and(AnnonceSpecifications.createdAfter(fromDate));
        if (toDate != null) spec = spec.and(AnnonceSpecifications.createdBefore(toDate));
        return annonceRepo.findAll(spec, pageable).map(mapper::toDTO);
    }

    public AnnonceDTO getById(Long id) {
        return mapper.toDTO(annonceRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Annonce introuvable (id=" + id + ")")));
    }

    @Transactional
    public AnnonceDTO create(AnnonceCreateDTO dto, Long currentUserId) {
        User author = userRepo.findById(currentUserId)
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
        Category category = categoryRepo.findById(dto.categoryId())
                .orElseThrow(() -> new NotFoundException("Categorie introuvable"));
        Annonce a = mapper.toEntity(dto);
        a.setAuthor(author);
        a.setCategory(category);
        a.setStatus(AnnonceStatus.DRAFT);
        return mapper.toDTO(annonceRepo.save(a));
    }

    @Transactional
    public AnnonceDTO update(Long id, AnnonceCreateDTO dto, Long currentUserId) {
        Annonce a = annonceRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Annonce introuvable"));
        checkAuthor(a, currentUserId);
        checkNotPublished(a);
        a.setTitle(dto.title()); a.setDescription(dto.description());
        a.setAdress(dto.adress()); a.setMail(dto.mail());
        if (dto.categoryId() != null) {
            a.setCategory(categoryRepo.findById(dto.categoryId())
                    .orElseThrow(() -> new NotFoundException("Categorie introuvable")));
        }
        return mapper.toDTO(annonceRepo.save(a));
    }

    @Transactional
    public AnnonceDTO patch(Long id, AnnoncePatchDTO dto, Long currentUserId) {
        Annonce a = annonceRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Annonce introuvable"));
        checkAuthor(a, currentUserId);
        checkNotPublished(a);
        mapper.updateFromPatch(dto, a);
        if (dto.categoryId() != null) {
            a.setCategory(categoryRepo.findById(dto.categoryId())
                    .orElseThrow(() -> new NotFoundException("Categorie introuvable")));
        }
        return mapper.toDTO(annonceRepo.save(a));
    }

    @Transactional
    public AnnonceDTO publish(Long id) {
        Annonce a = annonceRepo.findById(id).orElseThrow(() -> new NotFoundException("Annonce introuvable"));
        a.setStatus(AnnonceStatus.PUBLISHED);
        return mapper.toDTO(annonceRepo.save(a));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public AnnonceDTO archive(Long id) {
        Annonce a = annonceRepo.findById(id).orElseThrow(() -> new NotFoundException("Annonce introuvable"));
        a.setStatus(AnnonceStatus.ARCHIVED);
        return mapper.toDTO(annonceRepo.save(a));
    }

    @Transactional
    public void delete(Long id, Long currentUserId) {
        Annonce a = annonceRepo.findByIdWithRelations(id)
                .orElseThrow(() -> new NotFoundException("Annonce introuvable"));
        checkAuthor(a, currentUserId);
        if (a.getStatus() != AnnonceStatus.ARCHIVED)
            throw new ConflictException("L'annonce doit etre archivee avant suppression (statut: " + a.getStatus() + ")");
        annonceRepo.delete(a);
    }

    private void checkAuthor(Annonce a, Long userId) {
        if (!a.getAuthor().getId().equals(userId))
            throw new ForbiddenException("Seul l'auteur peut modifier/supprimer cette annonce");
    }
    private void checkNotPublished(Annonce a) {
        if (a.getStatus() == AnnonceStatus.PUBLISHED)
            throw new ConflictException("Une annonce publiee ne peut plus etre modifiee");
    }
}
