package com.master.air.repository;

import com.master.air.model.Annonce;
import com.master.air.model.AnnonceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    @Query("select a from Annonce a where a.status = :status")
    Page<Annonce> findByStatus(@Param("status") AnnonceStatus status, Pageable pageable);
}