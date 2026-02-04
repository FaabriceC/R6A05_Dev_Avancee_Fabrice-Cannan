package com.master.air.dao;

import com.master.air.model.Annonce;
import com.master.air.util.ConnectionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AnnonceDAO implements DAO<Annonce> {
    
    private Connection connection;

    public AnnonceDAO() throws ClassNotFoundException, SQLException {
        this.connection = ConnectionDB.getInstance();
    }

    @Override
    public Annonce create(Annonce annonce) throws SQLException {
        String sql = "INSERT INTO annonce (title, description, adress, mail, date) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, annonce.getTitle());
            pstmt.setString(2, annonce.getDescription());
            pstmt.setString(3, annonce.getAdress());
            pstmt.setString(4, annonce.getMail());
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        annonce.setId(generatedKeys.getInt(1));
                        annonce.setDate(new Timestamp(System.currentTimeMillis()));
                    }
                }
            }
            
            System.out.println("✓ Annonce créée : " + annonce.getTitle());
            return annonce;
        }
    }

    @Override
    public Annonce findById(int id) throws SQLException {
        String sql = "SELECT * FROM annonce WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractAnnonceFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    @Override
    public List<Annonce> findAll() throws SQLException {
        List<Annonce> annonces = new ArrayList<>();
        String sql = "SELECT * FROM annonce ORDER BY date DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                annonces.add(extractAnnonceFromResultSet(rs));
            }
        }
        
        System.out.println("✓ " + annonces.size() + " annonce(s) trouvée(s)");
        return annonces;
    }

    @Override
    public boolean update(Annonce annonce) throws SQLException {
        String sql = "UPDATE annonce SET title = ?, description = ?, adress = ?, mail = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, annonce.getTitle());
            pstmt.setString(2, annonce.getDescription());
            pstmt.setString(3, annonce.getAdress());
            pstmt.setString(4, annonce.getMail());
            pstmt.setInt(5, annonce.getId());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✓ Annonce mise à jour : ID=" + annonce.getId());
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM annonce WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✓ Annonce supprimée : ID=" + id);
                return true;
            }
        }
        
        return false;
    }

    private Annonce extractAnnonceFromResultSet(ResultSet rs) throws SQLException {
        return new Annonce(
            rs.getInt("id"),
            rs.getString("title"),
            rs.getString("description"),
            rs.getString("adress"),
            rs.getString("mail"),
            rs.getTimestamp("date")
        );
    }
}
