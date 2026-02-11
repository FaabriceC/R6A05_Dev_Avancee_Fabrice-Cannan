package com.master.air.service;

import com.master.air.model.*;
import com.master.air.repository.AnnonceRepository;
import com.master.air.repository.CategoryRepository;
import com.master.air.repository.UserRepository;
import com.master.air.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour les annonces.
 * Les transactions JPA sont gérées ici (et non dans les Servlets).
 *
 * Exercice 4 – Couche Service & Transactions
 */
public class AnnonceService {

    private static final Logger log = LoggerFactory.getLogger(AnnonceService.class);
    private static final int DEFAULT_PAGE_SIZE = 6;

    // ==================== Création ====================

    public Annonce createAnnonce(String title, String description, String adress,
                                  String mail, Long authorId, Long categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            UserRepository userRepo = new UserRepository(em);
            CategoryRepository catRepo = new CategoryRepository(em);
            AnnonceRepository annonceRepo = new AnnonceRepository(em);

            User author = userRepo.findById(authorId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable (id=" + authorId + ")"));
            Category category = catRepo.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable (id=" + categoryId + ")"));

            Annonce annonce = new Annonce();
            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            annonce.setStatus(AnnonceStatus.DRAFT);
            annonce.setAuthor(author);
            annonce.setCategory(category);

            annonceRepo.create(annonce);
            tx.commit();
            log.info("Annonce créée : {} (id={})", annonce.getTitle(), annonce.getId());
            return annonce;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur création annonce", e);
            throw new RuntimeException("Erreur lors de la création de l'annonce : " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ==================== Modification ====================

    public Annonce updateAnnonce(Long annonceId, String title, String description,
                                  String adress, String mail, Long categoryId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            AnnonceRepository annonceRepo = new AnnonceRepository(em);
            CategoryRepository catRepo = new CategoryRepository(em);

            Annonce annonce = annonceRepo.findById(annonceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable (id=" + annonceId + ")"));

            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);

            if (categoryId != null) {
                Category category = catRepo.findById(categoryId)
                        .orElseThrow(() -> new IllegalArgumentException("Catégorie introuvable"));
                annonce.setCategory(category);
            }

            annonceRepo.update(annonce);
            tx.commit();
            log.info("Annonce modifiée : id={}", annonceId);
            return annonce;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur modification annonce", e);
            throw new RuntimeException("Erreur lors de la modification : " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ==================== Publication ====================

    public Annonce publishAnnonce(Long annonceId) {
        return changeStatus(annonceId, AnnonceStatus.PUBLISHED, "publication");
    }

    // ==================== Archivage ====================

    public Annonce archiveAnnonce(Long annonceId) {
        return changeStatus(annonceId, AnnonceStatus.ARCHIVED, "archivage");
    }

    private Annonce changeStatus(Long annonceId, AnnonceStatus newStatus, String action) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AnnonceRepository repo = new AnnonceRepository(em);

            Annonce annonce = repo.findById(annonceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

            annonce.setStatus(newStatus);
            repo.update(annonce);
            tx.commit();
            log.info("Annonce {} : id={}", action, annonceId);
            return annonce;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur {} annonce", action, e);
            throw new RuntimeException("Erreur lors de l'" + action + " : " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ==================== Suppression ====================

    public void deleteAnnonce(Long annonceId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AnnonceRepository repo = new AnnonceRepository(em);

            Annonce annonce = repo.findById(annonceId)
                    .orElseThrow(() -> new IllegalArgumentException("Annonce introuvable"));

            repo.delete(annonce);
            tx.commit();
            log.info("Annonce supprimée : id={}", annonceId);
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur suppression annonce", e);
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // ==================== Recherche & listing paginé ====================

    public PaginatedResult<Annonce> listAnnonces(int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            AnnonceRepository repo = new AnnonceRepository(em);
            return repo.findAllPaginated(page, pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE);
        } finally {
            em.close();
        }
    }

    public PaginatedResult<Annonce> searchAnnonces(String keyword, int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            AnnonceRepository repo = new AnnonceRepository(em);
            return repo.searchByKeyword(keyword, page, pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE);
        } finally {
            em.close();
        }
    }

    public PaginatedResult<Annonce> filterAnnonces(Long categoryId, AnnonceStatus status, int page, int pageSize) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            AnnonceRepository repo = new AnnonceRepository(em);
            return repo.findByFilter(categoryId, status, page, pageSize > 0 ? pageSize : DEFAULT_PAGE_SIZE);
        } finally {
            em.close();
        }
    }

    public Optional<Annonce> getAnnonce(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            AnnonceRepository repo = new AnnonceRepository(em);
            return repo.findByIdWithRelations(id);
        } finally {
            em.close();
        }
    }

    public List<Annonce> getAnnoncesByAuthor(Long authorId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            AnnonceRepository repo = new AnnonceRepository(em);
            return repo.findByAuthorId(authorId);
        } finally {
            em.close();
        }
    }
}
