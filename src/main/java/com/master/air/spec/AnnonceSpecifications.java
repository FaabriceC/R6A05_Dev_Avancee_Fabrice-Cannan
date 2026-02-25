package com.master.air.spec;

import com.master.air.model.Annonce;
import com.master.air.model.AnnonceStatus;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;

public final class AnnonceSpecifications {

    private AnnonceSpecifications() {}

    public static Specification<Annonce> keywordLike(String q) {
        if (q == null || q.isBlank()) return null;

        String like = "%" + q.trim().toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), like),
                cb.like(cb.lower(root.get("description")), like)
        );
    }

    public static Specification<Annonce> hasStatus(AnnonceStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Annonce> hasCategoryId(Long categoryId) {
        if (categoryId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Annonce> hasAuthorId(Long authorId) {
        if (authorId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Annonce> fromDate(Timestamp from) {
        if (from == null) return null;
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), from);
    }

    public static Specification<Annonce> toDate(Timestamp to) {
        if (to == null) return null;
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), to);
    }
}