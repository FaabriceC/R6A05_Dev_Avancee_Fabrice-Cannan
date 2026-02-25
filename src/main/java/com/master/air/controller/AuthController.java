package com.master.air.controller;

import com.master.air.dto.*;
import com.master.air.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentification", description = "Login JWT")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService as) { this.authService = as; }

    @PostMapping("/login")
    @Operation(summary = "Authentification - retourne un token JWT signe")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
