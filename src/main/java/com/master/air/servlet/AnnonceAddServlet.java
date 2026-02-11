package com.master.air.servlet;

import com.master.air.model.Category;
import com.master.air.model.User;
import com.master.air.service.AnnonceService;
import com.master.air.service.CategoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AnnonceAdd", urlPatterns = {"/annonce/add"})
public class AnnonceAddServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        List<Category> categories = categoryService.findAll();
        req.setAttribute("categories", categories);
        req.getRequestDispatcher("/WEB-INF/views/AnnonceAdd.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String adress = req.getParameter("adress");
        String mail = req.getParameter("mail");
        String categoryIdStr = req.getParameter("categoryId");

        List<String> errors = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            errors.add("Le titre est obligatoire");
        } else if (title.length() > 64) {
            errors.add("Le titre ne doit pas dépasser 64 caractères");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.add("La description est obligatoire");
        } else if (description.length() > 256) {
            errors.add("La description ne doit pas dépasser 256 caractères");
        }
        if (adress == null || adress.trim().isEmpty()) {
            errors.add("L'adresse est obligatoire");
        }
        if (mail == null || mail.trim().isEmpty()) {
            errors.add("L'email est obligatoire");
        } else if (!mail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.add("L'email n'est pas valide");
        }

        Long categoryId = null;
        if (categoryIdStr == null || categoryIdStr.isBlank()) {
            errors.add("La catégorie est obligatoire");
        } else {
            try {
                categoryId = Long.parseLong(categoryIdStr);
            } catch (NumberFormatException e) {
                errors.add("Catégorie invalide");
            }
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("title", title);
            req.setAttribute("description", description);
            req.setAttribute("adress", adress);
            req.setAttribute("mail", mail);
            req.setAttribute("selectedCategoryId", categoryId);
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/AnnonceAdd.jsp").forward(req, resp);
            return;
        }

        try {
            User currentUser = (User) req.getSession().getAttribute("user");
            annonceService.createAnnonce(title.trim(), description.trim(),
                    adress.trim(), mail.trim(), currentUser.getId(), categoryId);

            req.getSession().setAttribute("success", "Annonce créée avec succès !");
            resp.sendRedirect(req.getContextPath() + "/annonce/list");
        } catch (Exception e) {
            errors.add("Erreur lors de la création : " + e.getMessage());
            req.setAttribute("errors", errors);
            req.setAttribute("title", title);
            req.setAttribute("description", description);
            req.setAttribute("adress", adress);
            req.setAttribute("mail", mail);
            req.setAttribute("selectedCategoryId", categoryId);
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/AnnonceAdd.jsp").forward(req, resp);
        }
    }
}
