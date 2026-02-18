package com.master.air.service;

import com.master.air.exception.ConflictException;
import com.master.air.exception.ForbiddenException;
import com.master.air.exception.NotFoundException;
import com.master.air.model.*;
import com.master.air.repository.AnnonceRepository;
import com.master.air.repository.CategoryRepository;
import com.master.air.repository.UserRepository;
import com.master.air.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class AnnonceService {

    private static final Logger log = LoggerFactory.getLogger(AnnonceService.class);
    private static final int DEFAULT_PAGE_SIZE = 6;


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


    public Annonce updateAnnonce(Long annonceId, String title, String description,
                                  String adress, String mail, Long categoryId, Long currentUserId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            AnnonceRepository annonceRepo = new AnnonceRepository(em);
            CategoryRepository catRepo = new CategoryRepository(em);

            Annonce annonce = annonceRepo.findByIdWithRelations(annonceId)
                    .orElseThrow(() -> new NotFoundException("Annonce introuvable (id=" + annonceId + ")"));

            if (!annonce.getAuthor().getId().equals(currentUserId)) {
                throw new ForbiddenException("Seul l'auteur peut modifier cette annonce");
            }

            if (annonce.getStatus() == AnnonceStatus.PUBLISHED) {
                throw new ConflictException("Une annonce publiee ne peut plus etre modifiee");
            }

            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);

            if (categoryId != null) {
                Category category = catRepo.findById(categoryId)
                        .orElseThrow(() -> new NotFoundException("Categorie introuvable"));
                annonce.setCategory(category);
            }

            annonceRepo.update(annonce);
            tx.commit();
            log.info("Annonce modifiee : id={}", annonceId);
            return annonce;
        } catch (OptimisticLockException e) {
            if (tx.isActive()) tx.rollback();
            throw new ConflictException("Conflit de concurrence : l'annonce a ete modifiee par un autre utilisateur");
        } catch (NotFoundException | ForbiddenException | ConflictException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur modification annonce", e);
            throw new RuntimeException("Erreur lors de la modification : " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    public Annonce publishAnnonce(Long annonceId) {
        return changeStatus(annonceId, AnnonceStatus.PUBLISHED, "publication");
    }


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
                    .orElseThrow(() -> new NotFoundException("Annonce introuvable (id=" + annonceId + ")"));

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


    public void deleteAnnonce(Long annonceId, Long currentUserId) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AnnonceRepository repo = new AnnonceRepository(em);

            Annonce annonce = repo.findByIdWithRelations(annonceId)
                    .orElseThrow(() -> new NotFoundException("Annonce introuvable"));

            if (!annonce.getAuthor().getId().equals(currentUserId)) {
                throw new ForbiddenException("Seul l'auteur peut supprimer cette annonce");
            }

            if (annonce.getStatus() != AnnonceStatus.ARCHIVED) {
                throw new ConflictException("L'annonce doit etre archivee avant d'etre supprimee (statut actuel: " + annonce.getStatus() + ")");
            }

            repo.delete(annonce);
            tx.commit();
            log.info("Annonce supprimee : id={}", annonceId);
        } catch (NotFoundException | ForbiddenException | ConflictException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur suppression annonce", e);
            throw new RuntimeException("Erreur lors de la suppression : " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


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
