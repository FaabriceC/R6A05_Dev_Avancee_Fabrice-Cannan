package com.master.air.service;

import com.master.air.dto.AnnonceCreateDTO;
import com.master.air.exception.*;
import com.master.air.mapper.AnnonceMapper;
import com.master.air.model.*;
import com.master.air.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceTest {

    @Mock AnnonceRepository annonceRepo;
    @Mock UserRepository userRepo;
    @Mock CategoryRepository categoryRepo;
    @Mock AnnonceMapper mapper;
    @InjectMocks AnnonceService service;

    private Annonce annonce(Long id, Long authorId, AnnonceStatus status) {
        Annonce a = new Annonce(); a.setId(id); a.setStatus(status);
        User u = new User(); u.setId(authorId); a.setAuthor(u);
        Category c = new Category("Cat"); c.setId(1L); a.setCategory(c);
        return a;
    }

    @Test @DisplayName("Seul l'auteur peut modifier -> ForbiddenException")
    void onlyAuthorCanUpdate() {
        when(annonceRepo.findByIdWithRelations(1L)).thenReturn(Optional.of(annonce(1L, 10L, AnnonceStatus.DRAFT)));
        assertThrows(ForbiddenException.class, () ->
            service.update(1L, new AnnonceCreateDTO("T","D","A","m@t.com",1L), 99L));
    }

    @Test @DisplayName("PUBLISHED ne peut etre modifie -> ConflictException")
    void publishedCannotUpdate() {
        when(annonceRepo.findByIdWithRelations(1L)).thenReturn(Optional.of(annonce(1L, 10L, AnnonceStatus.PUBLISHED)));
        assertThrows(ConflictException.class, () ->
            service.update(1L, new AnnonceCreateDTO("T","D","A","m@t.com",1L), 10L));
    }

    @Test @DisplayName("Suppression requiert ARCHIVED -> ConflictException")
    void deleteRequiresArchived() {
        when(annonceRepo.findByIdWithRelations(1L)).thenReturn(Optional.of(annonce(1L, 10L, AnnonceStatus.DRAFT)));
        assertThrows(ConflictException.class, () -> service.delete(1L, 10L));
    }

    @Test @DisplayName("Suppression par non-auteur -> ForbiddenException")
    void deleteOnlyByAuthor() {
        when(annonceRepo.findByIdWithRelations(1L)).thenReturn(Optional.of(annonce(1L, 10L, AnnonceStatus.ARCHIVED)));
        assertThrows(ForbiddenException.class, () -> service.delete(1L, 99L));
    }

    @Test @DisplayName("Annonce introuvable -> NotFoundException")
    void notFound() {
        when(annonceRepo.findByIdWithRelations(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getById(999L));
    }

    @Test @DisplayName("Suppression OK si auteur + ARCHIVED")
    void deleteOk() {
        Annonce a = annonce(1L, 10L, AnnonceStatus.ARCHIVED);
        when(annonceRepo.findByIdWithRelations(1L)).thenReturn(Optional.of(a));
        assertDoesNotThrow(() -> service.delete(1L, 10L));
        verify(annonceRepo).delete(a);
    }
}
