package com.master.air.servlet;

import com.master.air.dao.AnnonceDAO;
import com.master.air.model.Annonce;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "AnnonceDelete", urlPatterns = {"/annonce/delete"})
public class AnnonceDeleteServlet extends HttpServlet {
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            AnnonceDAO dao = new AnnonceDAO();
            Annonce annonce = dao.findById(id);
            
            if (annonce == null) {
                request.getSession().setAttribute("error", "Annonce introuvable");
                response.sendRedirect(request.getContextPath() + "/annonce/list");
                return;
            }
            
            request.setAttribute("annonce", annonce);
            request.getRequestDispatcher("/WEB-INF/views/AnnonceDelete.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/annonce/list");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la récupération de l'annonce : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            AnnonceDAO dao = new AnnonceDAO();
            boolean deleted = dao.delete(id);
            
            if (deleted) {
                request.getSession().setAttribute("success", "Annonce supprimée avec succès !");
            } else {
                request.getSession().setAttribute("error", "Échec de la suppression de l'annonce");
            }
            
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error", "ID invalide");
            response.sendRedirect(request.getContextPath() + "/annonce/list");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/annonce/list");
        }
    }
}
