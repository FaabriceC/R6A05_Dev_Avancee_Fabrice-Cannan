<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Erreur - Master Annonce</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="container">
    <h1>Une erreur est survenue</h1>
    <div class="alert alert-error">
        <c:choose>
            <c:when test="${not empty error}"><p>${error}</p></c:when>
            <c:otherwise><p>Une erreur inattendue s'est produite.</p></c:otherwise>
        </c:choose>
    </div>
    <a href="${pageContext.request.contextPath}/annonce/list" class="btn btn-primary">Retour à la liste</a>
</div>
</body>
</html>
