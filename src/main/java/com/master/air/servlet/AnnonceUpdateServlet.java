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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AnnonceUpdate", urlPatterns = {"/annonce/update"})
public class AnnonceUpdateServlet extends HttpServlet {

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
                request.setAttribute("error", "Annonce introuvable");
                response.sendRedirect(request.getContextPath() + "/annonce/list");
                return;
            }
            
            request.setAttribute("annonce", annonce);
            request.getRequestDispatcher("/WEB-INF/views/AnnonceUpdate.jsp").forward(request, response);
            
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
        
        request.setCharacterEncoding("UTF-8");
        
        String idParam = request.getParameter("id");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");
        
        List<String> errors = new ArrayList<>();
        
        if (idParam == null || idParam.trim().isEmpty()) {
            errors.add("ID manquant");
        }
        
        if (title == null || title.trim().isEmpty()) {
            errors.add("Le titre est obligatoire");
        }
        
        if (description == null || description.trim().isEmpty()) {
            errors.add("La description est obligatoire");
        }
        
        if (adress == null || adress.trim().isEmpty()) {
            errors.add("L'adresse est obligatoire");
        }
        
        if (mail == null || mail.trim().isEmpty()) {
            errors.add("L'email est obligatoire");
        } else if (!mail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("L'email n'est pas valide");
        }
        
        if (!errors.isEmpty()) {
            request.setAttribute("errors", errors);
            Annonce annonce = new Annonce();
            try {
                annonce.setId(Integer.parseInt(idParam));
            } catch (NumberFormatException e) {
                // ignore
            }
            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            request.setAttribute("annonce", annonce);
            request.getRequestDispatcher("/WEB-INF/views/AnnonceUpdate.jsp").forward(request, response);
            return;
        }
        
        try {
            int id = Integer.parseInt(idParam);
            
            AnnonceDAO dao = new AnnonceDAO();
            Annonce existingAnnonce = dao.findById(id);
            
            if (existingAnnonce == null) {
                errors.add("Annonce introuvable");
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
                return;
            }
            
            existingAnnonce.setTitle(title);
            existingAnnonce.setDescription(description);
            existingAnnonce.setAdress(adress);
            existingAnnonce.setMail(mail);
            
            boolean updated = dao.update(existingAnnonce);
            
            if (updated) {
                request.getSession().setAttribute("success", "Annonce modifiée avec succès !");
                response.sendRedirect(request.getContextPath() + "/annonce/list");
            } else {
                errors.add("Échec de la mise à jour");
                request.setAttribute("errors", errors);
                request.setAttribute("annonce", existingAnnonce);
                request.getRequestDispatcher("/WEB-INF/views/AnnonceUpdate.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            errors.add("ID invalide");
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            errors.add("Erreur lors de la mise à jour : " + e.getMessage());
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
