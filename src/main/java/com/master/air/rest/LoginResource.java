package com.master.air.rest;

import com.master.air.dto.ErrorResponse;
import com.master.air.dto.LoginDTO;
import com.master.air.dto.LoginResponseDTO;
import com.master.air.security.TokenStore;
import com.master.air.security.jaas.JaasBootstrap;
import com.master.air.security.jaas.UserPrincipal;
import com.master.air.security.jaas.UsernamePasswordCallbackHandler;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

/**
 * Endpoint d'authentification stateless.
 *
 * Exercice 5 - Authentification stateless
 * Bonus JAAS: utilise LoginContext("MasterAnnonceLogin") pour etablir l'identite (Subject/Principals).
 *
 * POST /api/login -> {token, userId, username}
 */
@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    private static final Logger log = LoggerFactory.getLogger(LoginResource.class);

    @POST
    public Response login(@Valid LoginDTO dto) {
        JaasBootstrap.ensureConfigured();

        try {
            LoginContext lc = new LoginContext(
                    "MasterAnnonceLogin",
                    new UsernamePasswordCallbackHandler(dto.getUsername(), dto.getPassword())
            );
            lc.login();

            Subject subject = lc.getSubject();
            UserPrincipal up = subject.getPrincipals(UserPrincipal.class)
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (up == null) {
                // Defensive: ne devrait pas arriver si le LoginModule est correct
                return Response.status(Response.Status.UNAUTHORIZED)
                        .entity(ErrorResponse.builder()
                                .status(401)
                                .error("Unauthorized")
                                .message("Authentification echouee")
                                .build())
                        .build();
            }

            String token = TokenStore.generateToken(up.getUserId(), up.getName());
            log.info("Login JAAS reussi pour {}", up.getName());

            return Response.ok(new LoginResponseDTO(token, up.getUserId(), up.getName())).build();

        } catch (LoginException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse.builder()
                            .status(401)
                            .error("Unauthorized")
                            .message("Nom d'utilisateur ou mot de passe incorrect")
                            .build())
                    .build();
        }
    }
}
