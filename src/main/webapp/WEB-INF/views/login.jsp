<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Connexion - Master Annonce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container auth-container">
        <h1>🔐 Connexion</h1>
        <p class="subtitle">Master Annonce - Plateforme d'annonces</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error"><p>${error}</p></div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login" class="form">
            <div class="form-group">
                <label for="username">Nom d'utilisateur</label>
                <input type="text" id="username" name="username" value="${username}" placeholder="Votre identifiant" required autofocus>
            </div>
            <div class="form-group">
                <label for="password">Mot de passe</label>
                <input type="password" id="password" name="password" placeholder="Votre mot de passe" required>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">Se connecter</button>
            </div>
        </form>

        <p class="auth-link">Pas encore de compte ? <a href="${pageContext.request.contextPath}/register">S'inscrire</a></p>
        <p class="auth-hint">Compte par défaut : <strong>admin / admin</strong></p>
    </div>
</body>
</html>
