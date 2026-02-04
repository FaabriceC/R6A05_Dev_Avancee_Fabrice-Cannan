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
import java.util.List;

@WebServlet(name = "AnnonceList", urlPatterns = {"/annonce/list", "/"})
public class AnnonceListServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            AnnonceDAO dao = new AnnonceDAO();
            List<Annonce> annonces = dao.findAll();
            
            request.setAttribute("annonces", annonces);
            
            String success = (String) request.getSession().getAttribute("success");
            if (success != null) {
                request.setAttribute("success", success);
                request.getSession().removeAttribute("success");
            }
            
            request.getRequestDispatcher("/WEB-INF/views/AnnonceList.jsp").forward(request, response);
            
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la récupération des annonces : " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/error.jsp").forward(request, response);
        }
    }
}
