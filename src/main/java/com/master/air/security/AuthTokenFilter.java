package com.master.air.security;

import com.master.air.dto.ErrorResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import java.io.IOException;
import java.util.Optional;


@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthTokenFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String path = ctx.getUriInfo().getPath();

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

        String token = authHeader.substring(7);
        Optional<TokenStore.TokenInfo> tokenInfo = TokenStore.validateToken(token);

        if (tokenInfo.isEmpty()) {
            log.warn("Token invalide ou expire pour {}", path);
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

        ctx.setProperty("userId", tokenInfo.get().getUserId());
        ctx.setProperty("username", tokenInfo.get().getUsername());
    }

    private boolean isPublicEndpoint(String path) {
        return path.equals("login")
                || path.equals("helloWorld")
                || path.startsWith("params");
    }
}
