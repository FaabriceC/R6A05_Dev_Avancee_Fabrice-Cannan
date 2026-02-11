package com.master.air.servlet;

import com.master.air.model.AnnonceStatus;
import com.master.air.model.Category;
import com.master.air.model.PaginatedResult;
import com.master.air.model.Annonce;
import com.master.air.service.AnnonceService;
import com.master.air.service.CategoryService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;


@WebServlet(name = "AnnonceList", urlPatterns = {"/annonce/list", "/"})
public class AnnonceListServlet extends HttpServlet {

    private final AnnonceService annonceService = new AnnonceService();
    private final CategoryService categoryService = new CategoryService();

    private static final int PAGE_SIZE = 6;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1;
        try {
            String p = req.getParameter("page");
            if (p != null) page = Math.max(1, Integer.parseInt(p));
        } catch (NumberFormatException ignored) {}

        String keyword = req.getParameter("keyword");
        String categoryIdStr = req.getParameter("categoryId");
        String statusStr = req.getParameter("status");

        Long categoryId = null;
        AnnonceStatus status = null;

        try {
            if (categoryIdStr != null && !categoryIdStr.isBlank()) {
                categoryId = Long.parseLong(categoryIdStr);
            }
        } catch (NumberFormatException ignored) {}

        try {
            if (statusStr != null && !statusStr.isBlank()) {
                status = AnnonceStatus.valueOf(statusStr);
            }
        } catch (IllegalArgumentException ignored) {}

        PaginatedResult<Annonce> result;
        if (keyword != null && !keyword.isBlank()) {
            result = annonceService.searchAnnonces(keyword.trim(), page, PAGE_SIZE);
        } else if (categoryId != null || status != null) {
            result = annonceService.filterAnnonces(categoryId, status, page, PAGE_SIZE);
        } else {
            result = annonceService.listAnnonces(page, PAGE_SIZE);
        }

        List<Category> categories = categoryService.findAll();

        req.setAttribute("annonces", result.getItems());
        req.setAttribute("totalCount", result.getTotalCount());
        req.setAttribute("currentPage", result.getPage());
        req.setAttribute("totalPages", result.getTotalPages());
        req.setAttribute("hasPrevious", result.hasPrevious());
        req.setAttribute("hasNext", result.hasNext());
        req.setAttribute("categories", categories);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedCategoryId", categoryId);
        req.setAttribute("selectedStatus", statusStr);

        String success = (String) req.getSession().getAttribute("success");
        if (success != null) {
            req.setAttribute("success", success);
            req.getSession().removeAttribute("success");
        }
        String error = (String) req.getSession().getAttribute("error");
        if (error != null) {
            req.setAttribute("error", error);
            req.getSession().removeAttribute("error");
        }

        req.getRequestDispatcher("/WEB-INF/views/AnnonceList.jsp").forward(req, resp);
    }
}
