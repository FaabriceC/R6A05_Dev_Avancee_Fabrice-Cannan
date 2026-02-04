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


@WebServlet(name = "AnnonceAdd", urlPatterns = {"/annonce/add"})
public class AnnonceAddServlet extends HttpServlet {
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("/WEB-INF/views/AnnonceAdd.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String adress = request.getParameter("adress");
        String mail = request.getParameter("mail");
        
        List<String> errors = new ArrayList<>();
        
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
            request.setAttribute("title", title);
            request.setAttribute("description", description);
            request.setAttribute("adress", adress);
            request.setAttribute("mail", mail);
            request.getRequestDispatcher("/WEB-INF/views/AnnonceAdd.jsp").forward(request, response);
            return;
        }
        
        try {
            Annonce annonce = new Annonce(title, description, adress, mail);
            AnnonceDAO dao = new AnnonceDAO();
            dao.create(annonce);
            
            request.getSession().setAttribute("success", "Annonce créée avec succès !");
            
            response.sendRedirect(request.getContextPath() + "/annonce/list");
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            errors.add("Erreur lors de l'enregistrement de l'annonce : " + e.getMessage());
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/AnnonceAdd.jsp").forward(request, response);
        }
    }
}
