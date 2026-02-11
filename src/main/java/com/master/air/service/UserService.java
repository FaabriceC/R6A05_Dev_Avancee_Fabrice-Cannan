package com.master.air.service;

import com.master.air.model.User;
import com.master.air.repository.UserRepository;
import com.master.air.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour les utilisateurs.
 * Gère l'authentification et l'inscription.
 */
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    /**
     * Authentifie un utilisateur par username et password.
     */
    public Optional<User> authenticate(String username, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            UserRepository repo = new UserRepository(em);
            return repo.findByUsernameAndPassword(username, password);
        } finally {
            em.close();
        }
    }

    /**
     * Inscrit un nouvel utilisateur.
     */
    public User register(String username, String email, String password) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            UserRepository repo = new UserRepository(em);

            if (repo.existsByUsername(username)) {
                throw new IllegalArgumentException("Ce nom d'utilisateur est déjà pris");
            }
            if (repo.existsByEmail(email)) {
                throw new IllegalArgumentException("Cet email est déjà utilisé");
            }

            User user = new User(username, email, password);
            repo.create(user);
            tx.commit();
            log.info("Utilisateur inscrit : {}", username);
            return user;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            log.error("Erreur inscription", e);
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Optional<User> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            UserRepository repo = new UserRepository(em);
            return repo.findById(id);
        } finally {
            em.close();
        }
    }

    public List<User> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            UserRepository repo = new UserRepository(em);
            return repo.findAll();
        } finally {
            em.close();
        }
    }
}
