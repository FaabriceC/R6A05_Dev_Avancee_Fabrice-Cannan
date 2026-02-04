<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.http.*" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des Annonces - Master Annonce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <header>
            <h1>🏠 Master Annonce</h1>
            <p class="subtitle">Plateforme de gestion d'annonces - Master 3IR</p>
        </header>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                ${success}
            </div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">
                ${error}
            </div>
        </c:if>
        
        <div class="actions-bar">
            <a href="${pageContext.request.contextPath}/annonce/add" class="btn btn-primary">
                Nouvelle annonce
            </a>
            <a href="${pageContext.request.contextPath}/helloname" class="btn btn-secondary">
                Hello Name
            </a>
        </div>
        
        <h2>Liste des annonces (${annonces.size()})</h2>
        
        <c:choose>
            <c:when test="${empty annonces}">
                <div class="empty-state">
                    <p>Aucune annonce disponible pour le moment.</p>
                    <a href="${pageContext.request.contextPath}/annonce/add" class="btn btn-primary">
                        Créer la première annonce
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="annonces-grid">
                    <c:forEach items="${annonces}" var="annonce">
                        <div class="annonce-card">
                            <div class="annonce-header">
                                <h3>${annonce.title}</h3>
                                <span class="annonce-date">
                                    <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy HH:mm" />
                                </span>
                            </div>
                            
                            <div class="annonce-body">
                                <p class="annonce-description">${annonce.description}</p>
                                
                                <div class="annonce-info">
                                    <p><strong>📍 Adresse :</strong> ${annonce.adress}</p>
                                    <p><strong>✉️ Contact :</strong> 
                                        <a href="mailto:${annonce.mail}">${annonce.mail}</a>
                                    </p>
                                </div>
                            </div>
                            
                            <div class="annonce-actions">
                                <a href="${pageContext.request.contextPath}/annonce/update?id=${annonce.id}" 
                                   class="btn btn-edit" 
                                   title="Modifier">
                                    ✏️ Modifier
                                </a>
                                <a href="${pageContext.request.contextPath}/annonce/delete?id=${annonce.id}" 
                                   class="btn btn-delete" 
                                   title="Supprimer">
                                    🗑️ Supprimer
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
        
        <footer>
            <p>BUT INFORMATIQUE - Fabrice CANNAN - Dev. Avancé - TP 1</p>
        </footer>
    </div>
</body>
</html>
