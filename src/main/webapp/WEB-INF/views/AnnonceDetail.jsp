<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${annonce.title} - Master Annonce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <a href="${pageContext.request.contextPath}/annonce/list" class="btn btn-secondary btn-sm">← Retour</a>

        <div class="annonce-preview" style="margin-top:20px">
            <div class="annonce-header">
                <h1>${annonce.title}</h1>
                <span class="badge badge-${annonce.status}">${annonce.status}</span>
            </div>
            <p class="annonce-date">
                Publiée le <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy à HH:mm" />
            </p>
            <span class="badge badge-category">${annonce.category.label}</span>

            <div class="annonce-details" style="margin-top:20px">
                <p><strong>Description :</strong> ${annonce.description}</p>
                <p><strong>📍 Adresse :</strong> ${annonce.adress}</p>
                <p><strong>✉️ Contact :</strong> <a href="mailto:${annonce.mail}">${annonce.mail}</a></p>
                <p><strong>👤 Auteur :</strong> ${annonce.author.username}</p>
            </div>

            <div class="annonce-actions" style="margin-top:20px">
                <a href="${pageContext.request.contextPath}/annonce/update?id=${annonce.id}" class="btn btn-edit">Modifier</a>

                <c:if test="${annonce.status == 'DRAFT'}">
                    <form method="post" action="${pageContext.request.contextPath}/annonce/status" style="display:inline">
                        <input type="hidden" name="id" value="${annonce.id}">
                        <input type="hidden" name="action" value="publish">
                        <button type="submit" class="btn btn-success">Publier</button>
                    </form>
                </c:if>
                <c:if test="${annonce.status == 'PUBLISHED'}">
                    <form method="post" action="${pageContext.request.contextPath}/annonce/status" style="display:inline">
                        <input type="hidden" name="id" value="${annonce.id}">
                        <input type="hidden" name="action" value="archive">
                        <button type="submit" class="btn btn-warning">Archiver</button>
                    </form>
                </c:if>

                <a href="${pageContext.request.contextPath}/annonce/delete?id=${annonce.id}" class="btn btn-delete">Supprimer</a>
            </div>
        </div>
    </div>
</body>
</html>
