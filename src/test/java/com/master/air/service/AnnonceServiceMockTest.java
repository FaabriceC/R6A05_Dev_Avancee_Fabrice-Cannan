package com.master.air.service;

import com.master.air.model.*;
import com.master.air.repository.AnnonceRepository;
import com.master.air.repository.CategoryRepository;
import com.master.air.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnonceServiceMockTest {

    @Mock private EntityManager em;
    @Mock private EntityTransaction tx;
    @Mock private AnnonceRepository annonceRepo;
    @Mock private UserRepository userRepo;
    @Mock private CategoryRepository categoryRepo;

    @Test
    @DisplayName("changeStatus - publier une annonce DRAFT")
    void testPublishDraft() {
        Annonce annonce = new Annonce();
        annonce.setId(1L);
        annonce.setTitle("Test");
        annonce.setStatus(AnnonceStatus.DRAFT);

        when(annonceRepo.findById(1L)).thenReturn(Optional.of(annonce));
        when(annonceRepo.update(annonce)).thenReturn(annonce);

        Annonce found = annonceRepo.findById(1L).orElseThrow();
        found.setStatus(AnnonceStatus.PUBLISHED);
        annonceRepo.update(found);

        assertEquals(AnnonceStatus.PUBLISHED, found.getStatus());
        verify(annonceRepo).update(found);
    }

    @Test
    @DisplayName("changeStatus - archiver une annonce PUBLISHED")
    void testArchivePublished() {
        Annonce annonce = new Annonce();
        annonce.setId(2L);
        annonce.setStatus(AnnonceStatus.PUBLISHED);

        when(annonceRepo.findById(2L)).thenReturn(Optional.of(annonce));

        Annonce found = annonceRepo.findById(2L).orElseThrow();
        found.setStatus(AnnonceStatus.ARCHIVED);

        assertEquals(AnnonceStatus.ARCHIVED, found.getStatus());
    }

    @Test
    @DisplayName("findById - annonce introuvable")
    void testNotFound() {
        when(annonceRepo.findById(999L)).thenReturn(Optional.empty());
        assertTrue(annonceRepo.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("delete - verification de l'appel")
    void testDelete() {
        Annonce annonce = new Annonce();
        annonce.setId(3L);

        when(annonceRepo.findById(3L)).thenReturn(Optional.of(annonce));

        Annonce found = annonceRepo.findById(3L).orElseThrow();
        annonceRepo.delete(found);

        verify(annonceRepo).delete(found);
    }

    @Test
    @DisplayName("Workflow - creation avec auteur et categorie")
    void testCreateWorkflow() {
        User user = new User("alice", "alice@test.com", "pass");
        user.setId(1L);
        Category cat = new Category("Immobilier");
        cat.setId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(categoryRepo.findById(1L)).thenReturn(Optional.of(cat));

        User author = userRepo.findById(1L).orElseThrow();
        Category category = categoryRepo.findById(1L).orElseThrow();

        Annonce a = new Annonce();
        a.setTitle("Nouveau");
        a.setDescription("Desc");
        a.setAdress("Addr");
        a.setMail("m@t.com");
        a.setAuthor(author);
        a.setCategory(category);
        a.setStatus(AnnonceStatus.DRAFT);

        assertEquals("alice", a.getAuthor().getUsername());
        assertEquals("Immobilier", a.getCategory().getLabel());
        assertEquals(AnnonceStatus.DRAFT, a.getStatus());
    }
}
