package com.master.air.servlet;

import com.master.air.service.CategoryService;
import com.master.air.service.UserService;
import com.master.air.util.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener d'initialisation de l'application.
 * Initialise JPA et crée les données par défaut.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppInitListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("=== Initialisation de MasterAnnonce ===");

        // Force la création de l'EntityManagerFactory
        JPAUtil.getEntityManagerFactory();
        log.info("JPA/Hibernate initialisé");

        // Catégories par défaut
        CategoryService categoryService = new CategoryService();
        categoryService.initDefaultCategories();

        // Utilisateur par défaut pour les tests
        UserService userService = new UserService();
        try {
            userService.register("admin", "admin@master.fr", "admin");
            log.info("Utilisateur admin créé");
        } catch (Exception e) {
            log.info("Utilisateur admin existe déjà ou erreur : {}", e.getMessage());
        }

        log.info("=== MasterAnnonce prêt ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.close();
        log.info("=== MasterAnnonce arrêté ===");
    }
}
