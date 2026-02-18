package com.master.air.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


public class JPAUtil {

    private static final String PERSISTENCE_UNIT = "MasterAnnoncePU";
    private static volatile EntityManagerFactory emf;

    private JPAUtil() {
        // Constructeur privé
    }


    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (JPAUtil.class) {
                if (emf == null) {
                    emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
                }
            }
        }
        return emf;
    }


    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }


    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
            emf = null;
        }
    }
}
