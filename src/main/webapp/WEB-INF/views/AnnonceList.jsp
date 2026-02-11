<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
        <div class="header-top">
            <div>
                <h1>🏠 Master Annonce</h1>
                <p class="subtitle">Plateforme de gestion d'annonces - BUT 3IR - JPA/Hibernate</p>
            </div>
            <div class="user-info">
                Connecté : <strong>${sessionScope.user.username}</strong>
                <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm">Déconnexion</a>
            </div>
        </div>
    </header>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <!-- Barre de recherche et filtres -->
    <div class="search-bar">
        <form method="get" action="${pageContext.request.contextPath}/annonce/list" class="search-form">
            <input type="text" name="keyword" value="${keyword}" placeholder="Rechercher par mot-clé...">
            <select name="categoryId">
                <option value="">-- Toutes catégories --</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="${cat.id}" ${cat.id == selectedCategoryId ? 'selected' : ''}>${cat.label}</option>
                </c:forEach>
            </select>
            <select name="status">
                <option value="">-- Tous statuts --</option>
                <option value="DRAFT" ${selectedStatus == 'DRAFT' ? 'selected' : ''}>Brouillon</option>
                <option value="PUBLISHED" ${selectedStatus == 'PUBLISHED' ? 'selected' : ''}>Publiée</option>
                <option value="ARCHIVED" ${selectedStatus == 'ARCHIVED' ? 'selected' : ''}>Archivée</option>
            </select>
            <button type="submit" class="btn btn-primary btn-sm">Filtrer</button>
            <a href="${pageContext.request.contextPath}/annonce/list" class="btn btn-secondary btn-sm">Réinitialiser</a>
        </form>
    </div>

    <div class="actions-bar">
        <a href="${pageContext.request.contextPath}/annonce/add" class="btn btn-primary">+ Nouvelle annonce</a>
        <span class="result-count">${totalCount} annonce(s) trouvée(s)</span>
    </div>

    <c:choose>
        <c:when test="${empty annonces}">
            <div class="empty-state">
                <p>Aucune annonce disponible.</p>
                <a href="${pageContext.request.contextPath}/annonce/add" class="btn btn-primary">Créer la première annonce</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="annonces-grid">
                <c:forEach items="${annonces}" var="annonce">
                    <div class="annonce-card">
                        <div class="annonce-header">
                            <h3><a href="${pageContext.request.contextPath}/annonce/detail?id=${annonce.id}">${annonce.title}</a></h3>
                            <span class="badge badge-${annonce.status}">${annonce.status}</span>
                        </div>
                        <div class="annonce-meta">
                            <span class="badge badge-category">${annonce.category.label}</span>
                            <span class="annonce-date">
                                    <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy HH:mm" />
                                </span>
                        </div>
                        <div class="annonce-body">
                            <p class="annonce-description">${annonce.description}</p>
                            <div class="annonce-info">
                                <p>📍 ${annonce.adress}</p>
                                <p>✉️ <a href="mailto:${annonce.mail}">${annonce.mail}</a></p>
                                <p>👤 ${annonce.author.username}</p>
                            </div>
                        </div>
                        <div class="annonce-actions">
                            <a href="${pageContext.request.contextPath}/annonce/detail?id=${annonce.id}" class="btn btn-sm btn-secondary">Détail</a>
                            <a href="${pageContext.request.contextPath}/annonce/update?id=${annonce.id}" class="btn btn-sm btn-edit">Modifier</a>

                            <c:if test="${annonce.status == 'DRAFT'}">
                                <form method="post" action="${pageContext.request.contextPath}/annonce/status" style="display:inline">
                                    <input type="hidden" name="id" value="${annonce.id}">
                                    <input type="hidden" name="action" value="publish">
                                    <button type="submit" class="btn btn-sm btn-success">Publier</button>
                                </form>
                            </c:if>
                            <c:if test="${annonce.status == 'PUBLISHED'}">
                                <form method="post" action="${pageContext.request.contextPath}/annonce/status" style="display:inline">
                                    <input type="hidden" name="id" value="${annonce.id}">
                                    <input type="hidden" name="action" value="archive">
                                    <button type="submit" class="btn btn-sm btn-warning">Archiver</button>
                                </form>
                            </c:if>

                            <a href="${pageContext.request.contextPath}/annonce/delete?id=${annonce.id}" class="btn btn-sm btn-delete">Supprimer</a>
                        </div>
                    </div>
                </c:forEach>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${hasPrevious}">
                        <a href="${pageContext.request.contextPath}/annonce/list?page=${currentPage - 1}&keyword=${keyword}&categoryId=${selectedCategoryId}&status=${selectedStatus}" class="btn btn-sm btn-secondary">← Précédent</a>
                    </c:if>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <a href="${pageContext.request.contextPath}/annonce/list?page=${i}&keyword=${keyword}&categoryId=${selectedCategoryId}&status=${selectedStatus}"
                           class="btn btn-sm ${i == currentPage ? 'btn-primary' : 'btn-secondary'}">${i}</a>
                    </c:forEach>
                    <c:if test="${hasNext}">
                        <a href="${pageContext.request.contextPath}/annonce/list?page=${currentPage + 1}&keyword=${keyword}&categoryId=${selectedCategoryId}&status=${selectedStatus}" class="btn btn-sm btn-secondary">Suivant →</a>
                    </c:if>
                </div>
            </c:if>
        </c:otherwise>
    </c:choose>

    <footer>
        <p>BUT INFORMATIQUE - Fabrice CANNAN - Dev. Avancé - TP 2 (JPA/Hibernate)</p>
    </footer>
</div>
</body>
</html>
