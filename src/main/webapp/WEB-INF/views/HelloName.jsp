<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.http.*" %><!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Hello the World - Avec nom</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="container">
        <h1>Hello the World</h1>
        
        <% if (request.getAttribute("name") != null && !((String)request.getAttribute("name")).isEmpty()) { %>
            <h2>Bonjour, <%= request.getAttribute("name") %> !</h2>
        <% } %>
        
        <form method="post" action="${pageContext.request.contextPath}/helloname">
            <div class="form-group">
                <label for="name">Votre nom :</label>
                <input type="text" id="name" name="name" required>
            </div>
            
            <button type="submit" class="btn btn-primary">Envoyer</button>
        </form>
        
        <br>
        <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">Retour à l'accueil</a>
    </div>
</body>
</html>
