package com.master.air.repository;

import com.master.air.model.Category;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;


public class CategoryRepository {

    private final EntityManager em;

    public CategoryRepository(EntityManager em) {
        this.em = em;
    }

    public Category create(Category category) {
        em.persist(category);
        return category;
    }

    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    public Optional<Category> findByLabel(String label) {
        try {
            Category cat = em.createQuery(
                    "SELECT c FROM Category c WHERE c.label = :label", Category.class)
                    .setParameter("label", label)
                    .getSingleResult();
            return Optional.of(cat);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    public List<Category> findAll() {
        return em.createQuery("SELECT c FROM Category c ORDER BY c.label ASC", Category.class)
                .getResultList();
    }

    public Category update(Category category) {
        return em.merge(category);
    }

    public void delete(Category category) {
        em.remove(em.contains(category) ? category : em.merge(category));
    }
}
