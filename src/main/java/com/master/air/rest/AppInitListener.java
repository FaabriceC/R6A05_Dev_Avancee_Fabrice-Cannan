package com.master.air.rest;

import com.master.air.service.CategoryService;
import com.master.air.service.UserService;
import com.master.air.util.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@WebListener
public class AppInitListener implements ServletContextListener {

    private static final Logger log = LoggerFactory.getLogger(AppInitListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("=== Initialisation de MasterAnnonce API ===");
        JPAUtil.getEntityManagerFactory();
        log.info("JPA/Hibernate initialise");

        new CategoryService().initDefaultCategories();

        try {
            new UserService().register("admin", "admin@master.fr", "admin");
            log.info("Utilisateur admin cree");
        } catch (Exception e) {
            log.info("Utilisateur admin existe deja");
        }

        log.info("=== MasterAnnonce API pret ===");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        JPAUtil.close();
        log.info("=== MasterAnnonce API arrete ===");
    }
}
