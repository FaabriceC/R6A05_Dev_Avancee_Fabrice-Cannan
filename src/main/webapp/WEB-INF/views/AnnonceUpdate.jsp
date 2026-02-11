<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Modifier l'annonce - Master Annonce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <h1>✏️ Modifier l'annonce</h1>

    <c:if test="${not empty errors}">
        <div class="alert alert-error">
            <ul><c:forEach items="${errors}" var="error"><li>${error}</li></c:forEach></ul>
        </div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/annonce/update" class="form">
        <input type="hidden" name="id" value="${annonce.id}">
        <div class="form-group">
            <label for="title">Titre <span class="required">*</span></label>
            <input type="text" id="title" name="title" maxlength="64" value="${annonce.title}" required>
        </div>
        <div class="form-group">
            <label for="description">Description <span class="required">*</span></label>
            <textarea id="description" name="description" rows="5" maxlength="256" required>${annonce.description}</textarea>
            <small>Maximum 256 caractères</small>
        </div>
        <div class="form-group">
            <label for="adress">Adresse <span class="required">*</span></label>
            <input type="text" id="adress" name="adress" maxlength="64" value="${annonce.adress}" required>
        </div>
        <div class="form-group">
            <label for="mail">Email de contact <span class="required">*</span></label>
            <input type="email" id="mail" name="mail" maxlength="64" value="${annonce.mail}" required>
        </div>
        <div class="form-group">
            <label for="categoryId">Catégorie <span class="required">*</span></label>
            <select id="categoryId" name="categoryId" required>
                <option value="">-- Sélectionnez --</option>
                <c:forEach items="${categories}" var="cat">
                    <option value="${cat.id}"
                        ${cat.id == annonce.category.id || cat.id == selectedCategoryId ? 'selected' : ''}>${cat.label}</option>
                </c:forEach>
            </select>
        </div>
        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Enregistrer</button>
            <a href="${pageContext.request.contextPath}/annonce/list" class="btn btn-secondary">Annuler</a>
        </div>
    </form>
</div>
</body>
</html>
