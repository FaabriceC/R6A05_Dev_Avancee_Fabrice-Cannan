package com.master.air.servlet;

import com.master.air.model.Annonce;
import com.master.air.service.AnnonceService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * Servlet affichant le détail d'une annonce.
 * Exercice 5 – Détail d'une annonce
 */
@WebServlet(name = "AnnonceDetail", urlPatterns = {"/annonce/detail"})
public class AnnonceDetailServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();

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
            req.getRequestDispatcher("/WEB-INF/views/AnnonceDetail.jsp").forward(req, resp);

        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/annonce/list");
        }
    }
}
