package com.master.air.service;

import com.master.air.model.Category;
import com.master.air.repository.CategoryRepository;
import com.master.air.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    public List<Category> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new CategoryRepository(em).findAll();
        } finally {
            em.close();
        }
    }

    public Optional<Category> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return new CategoryRepository(em).findById(id);
        } finally {
            em.close();
        }
    }

    public Category create(String label) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Category category = new Category(label);
            new CategoryRepository(em).create(category);
            tx.commit();
            log.info("Catégorie créée : {}", label);
            return category;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erreur création catégorie : " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }


    public void initDefaultCategories() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            CategoryRepository repo = new CategoryRepository(em);
            List<Category> existing = repo.findAll();
            if (existing.isEmpty()) {
                String[] defaults = {"Immobilier", "Emploi", "Véhicules", "Électronique", "Services", "Divers"};
                for (String label : defaults) {
                    repo.create(new Category(label));
                }
                log.info("Catégories par défaut initialisées");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur initialisation catégories", e);
        } finally {
            em.close();
        }
    }
}
