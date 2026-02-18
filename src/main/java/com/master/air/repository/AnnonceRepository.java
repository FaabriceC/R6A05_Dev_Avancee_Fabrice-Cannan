package com.master.air.repository;

import com.master.air.model.Annonce;
import com.master.air.model.AnnonceStatus;
import com.master.air.model.PaginatedResult;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class AnnonceRepository {

    private final EntityManager em;

    public AnnonceRepository(EntityManager em) {
        this.em = em;
    }


    public Annonce create(Annonce annonce) {
        em.persist(annonce);
        return annonce;
    }

    public Optional<Annonce> findById(Long id) {
        return Optional.ofNullable(em.find(Annonce.class, id));
    }

    public Optional<Annonce> findByIdWithRelations(Long id) {
        List<Annonce> results = em.createQuery(
                "SELECT a FROM Annonce a " +
                "JOIN FETCH a.author " +
                "JOIN FETCH a.category " +
                "WHERE a.id = :id", Annonce.class)
                .setParameter("id", id)
                .getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Annonce> findAll() {
        return em.createQuery(
                "SELECT a FROM Annonce a " +
                "JOIN FETCH a.author " +
                "JOIN FETCH a.category " +
                "ORDER BY a.date DESC", Annonce.class)
                .getResultList();
    }

    public Annonce update(Annonce annonce) {
        return em.merge(annonce);
    }

    public void delete(Annonce annonce) {
        em.remove(em.contains(annonce) ? annonce : em.merge(annonce));
    }


    public PaginatedResult<Annonce> searchByKeyword(String keyword, int page, int pageSize) {
        String likePattern = "%" + keyword + "%";

        Long count = em.createQuery(
                "SELECT COUNT(a) FROM Annonce a " +
                "WHERE LOWER(a.title) LIKE LOWER(:kw) OR LOWER(a.description) LIKE LOWER(:kw)",
                Long.class)
                .setParameter("kw", likePattern)
                .getSingleResult();

        List<Annonce> results = em.createQuery(
                "SELECT a FROM Annonce a " +
                "JOIN FETCH a.author " +
                "JOIN FETCH a.category " +
                "WHERE LOWER(a.title) LIKE LOWER(:kw) OR LOWER(a.description) LIKE LOWER(:kw) " +
                "ORDER BY a.date DESC", Annonce.class)
                .setParameter("kw", likePattern)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return new PaginatedResult<>(results, count, page, pageSize);
    }


    public PaginatedResult<Annonce> findByFilter(Long categoryId, AnnonceStatus status, int page, int pageSize) {
        StringBuilder jpqlCount = new StringBuilder("SELECT COUNT(a) FROM Annonce a WHERE 1=1");
        StringBuilder jpql = new StringBuilder(
                "SELECT a FROM Annonce a JOIN FETCH a.author JOIN FETCH a.category WHERE 1=1");

        if (categoryId != null) {
            jpqlCount.append(" AND a.category.id = :catId");
            jpql.append(" AND a.category.id = :catId");
        }
        if (status != null) {
            jpqlCount.append(" AND a.status = :status");
            jpql.append(" AND a.status = :status");
        }
        jpql.append(" ORDER BY a.date DESC");

        TypedQuery<Long> countQuery = em.createQuery(jpqlCount.toString(), Long.class);
        if (categoryId != null) countQuery.setParameter("catId", categoryId);
        if (status != null) countQuery.setParameter("status", status);
        Long count = countQuery.getSingleResult();

        TypedQuery<Annonce> dataQuery = em.createQuery(jpql.toString(), Annonce.class);
        if (categoryId != null) dataQuery.setParameter("catId", categoryId);
        if (status != null) dataQuery.setParameter("status", status);
        dataQuery.setFirstResult((page - 1) * pageSize);
        dataQuery.setMaxResults(pageSize);

        return new PaginatedResult<>(dataQuery.getResultList(), count, page, pageSize);
    }


    public PaginatedResult<Annonce> findAllPaginated(int page, int pageSize) {
        Long count = em.createQuery("SELECT COUNT(a) FROM Annonce a", Long.class)
                .getSingleResult();

        List<Annonce> results = em.createQuery(
                "SELECT a FROM Annonce a " +
                "JOIN FETCH a.author " +
                "JOIN FETCH a.category " +
                "ORDER BY a.date DESC", Annonce.class)
                .setFirstResult((page - 1) * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return new PaginatedResult<>(results, count, page, pageSize);
    }


    public List<Annonce> findByAuthorId(Long authorId) {
        return em.createQuery(
                "SELECT a FROM Annonce a " +
                "JOIN FETCH a.category " +
                "WHERE a.author.id = :authorId " +
                "ORDER BY a.date DESC", Annonce.class)
                .setParameter("authorId", authorId)
                .getResultList();
    }
}
