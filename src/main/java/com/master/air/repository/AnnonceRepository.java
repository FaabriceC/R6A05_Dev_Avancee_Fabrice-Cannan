package com.master.air.repository;

import com.master.air.model.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnnonceRepository extends JpaRepository<Annonce, Long>, JpaSpecificationExecutor<Annonce> {

    @Query("SELECT a FROM Annonce a JOIN FETCH a.author JOIN FETCH a.category WHERE a.id = :id")
    Optional<Annonce> findByIdWithRelations(Long id);
}
