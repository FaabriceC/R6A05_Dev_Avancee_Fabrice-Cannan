package com.master.air.service;

import com.master.air.dto.AnnonceCreateRequestDTO;
import com.master.air.dto.AnnonceDTO;
import com.master.air.dto.AnnonceUpdateRequestDTO;
import com.master.air.exception.NotFoundException;
import com.master.air.mapper.AnnonceMapper;
import com.master.air.model.Annonce;
import com.master.air.model.AnnonceStatus;
import com.master.air.repository.AnnonceRepository;
import com.master.air.repository.CategoryRepository;
import com.master.air.repository.UserRepository;
import com.master.air.security.AuthContext;
import com.master.air.spec.AnnonceSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnnonceMapper annonceMapper;

    public AnnonceService(
            AnnonceRepository annonceRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            AnnonceMapper annonceMapper
    ) {
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.annonceMapper = annonceMapper;
    }


    @Transactional(readOnly = true)
    public AnnonceDTO getById(Long id) {
        Annonce annonce = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found: " + id));
        return annonceMapper.toDto(annonce);
    }

    @Transactional(readOnly = true)
    public Page<AnnonceDTO> search(
            String q,
            AnnonceStatus status,
            Long categoryId,
            Long authorId,
            Long fromDate,
            Long toDate,
            Pageable pageable
    ) {
        Timestamp fromTs = (fromDate == null) ? null : new Timestamp(fromDate);
        Timestamp toTs = (toDate == null) ? null : new Timestamp(toDate);

        Specification<Annonce> spec = Specification.where(AnnonceSpecifications.keywordLike(q))
                .and(AnnonceSpecifications.hasStatus(status))
                .and(AnnonceSpecifications.hasCategoryId(categoryId))
                .and(AnnonceSpecifications.hasAuthorId(authorId))
                .and(AnnonceSpecifications.fromDate(fromTs))
                .and(AnnonceSpecifications.toDate(toTs));

        return annonceRepository.findAll(spec, pageable).map(annonceMapper::toDto);
    }


    @Transactional
    public AnnonceDTO create(AnnonceCreateRequestDTO dto) {
        var author = userRepository.findById(dto.authorId)
                .orElseThrow(() -> new NotFoundException("User not found: " + dto.authorId));

        var category = categoryRepository.findById(dto.categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.categoryId));

        Annonce entity = annonceMapper.toEntity(dto);
        entity.setAuthor(author);
        entity.setCategory(category);

        Annonce saved = annonceRepository.save(entity);
        return annonceMapper.toDto(saved);
    }


    @Transactional
    public AnnonceDTO update(Long id, AnnonceUpdateRequestDTO dto) {

        Annonce entity = annonceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Annonce not found: " + id));

        Long currentUserId = AuthContext.currentUserId();

        boolean isAdmin = AuthContext.hasRole("ROLE_ADMIN");
        boolean isAuthor = entity.getAuthor().getId().equals(currentUserId);

        if (entity.getStatus() == AnnonceStatus.PUBLISHED) {
            throw new IllegalArgumentException("Published annonces cannot be updated");
        }

        if (!isAdmin && !isAuthor) {
            throw new IllegalArgumentException("Only author can update this annonce");
        }

        if (dto.status == AnnonceStatus.ARCHIVED && !isAdmin) {
            throw new IllegalArgumentException("Only admin can archive an annonce");
        }

        var author = userRepository.findById(dto.authorId)
                .orElseThrow(() -> new NotFoundException("User not found: " + dto.authorId));

        var category = categoryRepository.findById(dto.categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found: " + dto.categoryId));

        annonceMapper.updateEntityFromDto(dto, entity);

        entity.setAuthor(author);
        entity.setCategory(category);

        Annonce saved = annonceRepository.save(entity);
        return annonceMapper.toDto(saved);
    }


    @Transactional
    public void delete(Long id) {
        if (!annonceRepository.existsById(id)) {
            throw new NotFoundException("Annonce not found: " + id);
        }
        annonceRepository.deleteById(id);
    }
}