package com.master.air.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe de gestion de la connexion à la base de données PostgreSQL
 * Implémentation du pattern Singleton (thread-safe)
 * @author Master 3IR
 */
public class ConnectionDB {
    
    private static final String URL = "jdbc:postgresql://localhost:5433/MasterAnnonce";
    private static final String USER = "myuser";
    private static final String PASSWORD = "mypassword";
    
    /**
     * Instance unique de la connexion (volatile pour garantir la visibilité entre threads)
     */
    private static volatile Connection connect;
    
    /**
     * Constructeur privé pour empêcher l'instanciation directe
     */
    private ConnectionDB() {
        // Constructeur privé
    }
    
    /**
     * Méthode pour obtenir l'instance unique de la connexion
     * Double-checked locking pour thread-safety et performance
     * @return Connection instance unique
     * @throws ClassNotFoundException si le driver PostgreSQL n'est pas trouvé
     * @throws SQLException si erreur de connexion
     */
    public static Connection getInstance() throws ClassNotFoundException, SQLException {
        if (connect == null) {
            synchronized (ConnectionDB.class) {
                if (connect == null) {
                    Class.forName("org.postgresql.Driver");
                    connect = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("✓ Connexion à la base de données établie");
                }
            }
        }
        return connect;
    }
    
    /**
     * Ferme la connexion à la base de données
     */
    public static void closeConnection() {
        if (connect != null) {
            try {
                connect.close();
                connect = null;
                System.out.println("✓ Connexion à la base de données fermée");
            } catch (SQLException e) {
                System.err.println("✗ Erreur lors de la fermeture de la connexion : " + e.getMessage());
            }
        }
    }
    
    /**
     * Teste la connexion à la base de données
     * @return true si la connexion est valide
     */
    public static boolean testConnection() {
        try {
            Connection conn = getInstance();
            return conn != null && !conn.isClosed();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("✗ Test de connexion échoué : " + e.getMessage());
            return false;
        }
    }
}
