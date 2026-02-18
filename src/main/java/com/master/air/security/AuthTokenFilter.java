package com.master.air.security;

import com.master.air.dto.ErrorResponse;
import com.master.air.security.jaas.*;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import java.io.IOException;

/**
 * Filtre JAX-RS de securite.
 *
 * Exercice 6 - Filtre de securite
 * Bonus JAAS: reconstruit l'identite a chaque requete via LoginContext("MasterAnnonceToken").
 *
 * Header attendu: Authorization: Bearer <token>
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthTokenFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();

        // Endpoints publics
        if (isPublicEndpoint(path)) {
            return;
        }

        String authHeader = ctx.getHeaderString("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Acces non authentifie a {}", path);
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse.builder()
                            .status(401)
                            .error("Unauthorized")
                            .message("Token d'authentification requis (header: Authorization: Bearer <token>)")
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build());
            return;
        }

        String token = authHeader.substring(7).trim();
        if (token.isBlank()) {
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse.builder()
                            .status(401)
                            .error("Unauthorized")
                            .message("Token vide")
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build());
            return;
        }

        // Bonus JAAS: validation token + reconstruction Subject
        JaasBootstrap.ensureConfigured();
        try {
            LoginContext lc = new LoginContext("MasterAnnonceToken", new TokenCallbackHandler(token));
            lc.login();

            Subject subject = lc.getSubject();
            UserPrincipal up = subject.getPrincipals(UserPrincipal.class).stream().findFirst().orElse(null);

            if (up == null) {
                ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponse.builder()
                                .status(401)
                                .error("Unauthorized")
                                .message("Token invalide ou expire")
                                .build())
                        .type(MediaType.APPLICATION_JSON)
                        .build());
                return;
            }

            // Attacher l'identite au contexte de la requete (compatible avec le reste du code)
            ctx.setProperty("userId", up.getUserId());
            ctx.setProperty("username", up.getName());

            // SecurityContext (roles)
            boolean secure = ctx.getUriInfo().getRequestUri().getScheme().equalsIgnoreCase("https");
            ctx.setSecurityContext(new JaasSecurityContext(subject, secure));

        } catch (LoginException e) {
            log.warn("Token invalide/expire pour {}: {}", path, e.getMessage());
            ctx.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse.builder()
                            .status(401)
                            .error("Unauthorized")
                            .message("Token invalide ou expire")
                            .build())
                    .type(MediaType.APPLICATION_JSON)
                    .build());
        }
    }

    private boolean isPublicEndpoint(String path) {
        // /api/login, /api/helloWorld, /api/params
        return path.equals("login")
                || path.equals("helloWorld")
                || path.startsWith("params");
    }
}
