package com.master.air.specification;

import com.master.air.model.Annonce;
import com.master.air.model.AnnonceStatus;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDateTime;

public final class AnnonceSpecifications {

    private AnnonceSpecifications() {}

    public static Specification<Annonce> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Annonce> hasStatus(AnnonceStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Annonce> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Annonce> hasAuthorId(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Annonce> createdAfter(LocalDateTime from) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), from);
    }

    public static Specification<Annonce> createdBefore(LocalDateTime to) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), to);
    }

    public static Specification<Annonce> fetchRelations() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("author", JoinType.LEFT);
                root.fetch("category", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }
}
