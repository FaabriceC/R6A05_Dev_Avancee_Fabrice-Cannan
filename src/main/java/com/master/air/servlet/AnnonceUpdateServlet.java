package com.master.air.servlet;

import com.master.air.model.Annonce;
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
import java.util.Optional;

@WebServlet(name = "AnnonceUpdate", urlPatterns = {"/annonce/update"})
public class AnnonceUpdateServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/annonce/list");
            return;
        }
        try {
            Long id = Long.parseLong(idParam);
            Optional<Annonce> annonceOpt = annonceService.getAnnonce(id);
            if (annonceOpt.isEmpty()) {
                req.getSession().setAttribute("error", "Annonce introuvable");
                resp.sendRedirect(req.getContextPath() + "/annonce/list");
                return;
            }
            req.setAttribute("annonce", annonceOpt.get());
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/AnnonceUpdate.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/annonce/list");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String idParam = req.getParameter("id");
        String title = req.getParameter("title");
        String description = req.getParameter("description");
        String adress = req.getParameter("adress");
        String mail = req.getParameter("mail");
        String categoryIdStr = req.getParameter("categoryId");

        List<String> errors = new ArrayList<>();

        Long annonceId = null;
        try {
            annonceId = Long.parseLong(idParam);
        } catch (NumberFormatException e) {
            errors.add("ID invalide");
        }

        if (title == null || title.trim().isEmpty()) {
            errors.add("Le titre est obligatoire");
        } else if (title.length() > 64) {
            errors.add("Le titre ne doit pas depasser 64 caracteres");
        }
        if (description == null || description.trim().isEmpty()) {
            errors.add("La description est obligatoire");
        } else if (description.length() > 256) {
            errors.add("La description ne doit pas depasser 256 caracteres");
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
            errors.add("La categorie est obligatoire");
        } else {
            try {
                categoryId = Long.parseLong(categoryIdStr);
            } catch (NumberFormatException e) {
                errors.add("Categorie invalide");
            }
        }

        if (!errors.isEmpty()) {
            Annonce annonce = new Annonce();
            annonce.setId(annonceId);
            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            req.setAttribute("annonce", annonce);
            req.setAttribute("errors", errors);
            req.setAttribute("selectedCategoryId", categoryId);
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/AnnonceUpdate.jsp").forward(req, resp);
            return;
        }

        try {
            annonceService.updateAnnonce(annonceId, title.trim(), description.trim(),
                    adress.trim(), mail.trim(), categoryId);
            req.getSession().setAttribute("success", "Annonce modifiee avec succes !");
            resp.sendRedirect(req.getContextPath() + "/annonce/list");
        } catch (Exception e) {
            errors.add("Erreur lors de la modification : " + e.getMessage());
            Annonce annonce = new Annonce();
            annonce.setId(annonceId);
            annonce.setTitle(title);
            annonce.setDescription(description);
            annonce.setAdress(adress);
            annonce.setMail(mail);
            req.setAttribute("annonce", annonce);
            req.setAttribute("errors", errors);
            req.setAttribute("categories", categoryService.findAll());
            req.getRequestDispatcher("/WEB-INF/views/AnnonceUpdate.jsp").forward(req, resp);
        }
    }
}
