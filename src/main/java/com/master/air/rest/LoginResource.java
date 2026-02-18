package com.master.air.rest;

import com.master.air.dto.ErrorResponse;
import com.master.air.dto.LoginDTO;
import com.master.air.dto.LoginResponseDTO;
import com.master.air.model.User;
import com.master.air.security.TokenStore;
import com.master.air.service.UserService;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    private static final Logger log = LoggerFactory.getLogger(LoginResource.class);
    private final UserService userService = new UserService();

    @POST
    public Response login(@Valid LoginDTO dto) {
        Optional<User> userOpt = userService.authenticate(dto.getUsername(), dto.getPassword());

        if (userOpt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(ErrorResponse.builder()
                            .status(401)
                            .error("Unauthorized")
                            .message("Nom d'utilisateur ou mot de passe incorrect")
                            .build())
                    .build();
        }

        User user = userOpt.get();
        String token = TokenStore.generateToken(user);
        log.info("Login reussi pour {}", user.getUsername());

        return Response.ok(new LoginResponseDTO(token, user.getId(), user.getUsername())).build();
    }
}
