<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Inscription - Master Annonce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container auth-container">
        <h1>📝 Inscription</h1>

        <c:if test="${not empty errors}">
            <div class="alert alert-error">
                <ul><c:forEach items="${errors}" var="e"><li>${e}</li></c:forEach></ul>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register" class="form">
            <div class="form-group">
                <label for="username">Nom d'utilisateur <span class="required">*</span></label>
                <input type="text" id="username" name="username" value="${username}" required>
            </div>
            <div class="form-group">
                <label for="email">Email <span class="required">*</span></label>
                <input type="email" id="email" name="email" value="${email}" required>
            </div>
            <div class="form-group">
                <label for="password">Mot de passe <span class="required">*</span></label>
                <input type="password" id="password" name="password" required>
                <small>Minimum 4 caractères</small>
            </div>
            <div class="form-group">
                <label for="confirmPassword">Confirmer le mot de passe <span class="required">*</span></label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>
            </div>
            <div class="form-actions">
                <button type="submit" class="btn btn-primary">S'inscrire</button>
            </div>
        </form>

        <p class="auth-link">Déjà un compte ? <a href="${pageContext.request.contextPath}/login">Se connecter</a></p>
    </div>
</body>
</html>
