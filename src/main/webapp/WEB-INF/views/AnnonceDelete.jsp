<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Supprimer l'annonce - Master Annonce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <h1>🗑️ Supprimer l'annonce</h1>
    <div class="alert alert-warning">
        <h3>⚠️ Attention !</h3>
        <p>Vous êtes sur le point de supprimer définitivement cette annonce. Cette action est irréversible.</p>
    </div>
    <div class="annonce-preview">
        <h2>${annonce.title}</h2>
        <p class="annonce-date">Créée le <fmt:formatDate value="${annonce.date}" pattern="dd/MM/yyyy à HH:mm" /></p>
        <div class="annonce-details">
            <p><strong>Description :</strong> ${annonce.description}</p>
            <p><strong>Adresse :</strong> ${annonce.adress}</p>
            <p><strong>Contact :</strong> ${annonce.mail}</p>
            <p><strong>Catégorie :</strong> ${annonce.category.label}</p>
            <p><strong>Statut :</strong> ${annonce.status}</p>
        </div>
    </div>
    <form method="post" action="${pageContext.request.contextPath}/annonce/delete" class="form">
        <input type="hidden" name="id" value="${annonce.id}">
        <div class="form-actions">
            <button type="submit" class="btn btn-danger">Confirmer la suppression</button>
            <a href="${pageContext.request.contextPath}/annonce/list" class="btn btn-secondary">Annuler</a>
        </div>
    </form>
</div>
</body>
</html>
