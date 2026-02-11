package com.master.air.servlet;

import com.master.air.service.AnnonceService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet gerant les changements de statut (Publish / Archive).
 * Exercice 5 - Actions Publish / Archive selon le statut
 */
@WebServlet(name = "AnnonceStatus", urlPatterns = {"/annonce/status"})
public class AnnonceStatusServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        String action = req.getParameter("action");

        if (idParam == null || action == null) {
            resp.sendRedirect(req.getContextPath() + "/annonce/list");
            return;
        }

        try {
            Long id = Long.parseLong(idParam);
            switch (action) {
                case "publish":
                    annonceService.publishAnnonce(id);
                    req.getSession().setAttribute("success", "Annonce publiee !");
                    break;
                case "archive":
                    annonceService.archiveAnnonce(id);
                    req.getSession().setAttribute("success", "Annonce archivee !");
                    break;
                default:
                    req.getSession().setAttribute("error", "Action inconnue");
            }
        } catch (Exception e) {
            req.getSession().setAttribute("error", "Erreur : " + e.getMessage());
        }

        // Redirect back to referrer or list
        String referer = req.getHeader("Referer");
        resp.sendRedirect(referer != null ? referer : req.getContextPath() + "/annonce/list");
    }
}
