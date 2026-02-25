package com.master.air.service;

import com.master.air.dto.LoginDTO;
import com.master.air.dto.LoginResponseDTO;
import com.master.air.model.User;
import com.master.air.repository.UserRepository;
import com.master.air.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository ur, JwtService js, PasswordEncoder pe) {
        this.userRepo = ur; this.jwtService = js; this.passwordEncoder = pe;
    }

    public LoginResponseDTO login(LoginDTO dto) {
        User user = userRepo.findByUsername(dto.username())
                .orElseThrow(() -> new IllegalArgumentException("Identifiants incorrects"));
        if (!passwordEncoder.matches(dto.password(), user.getPassword()))
            throw new IllegalArgumentException("Identifiants incorrects");
        String token = jwtService.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        return new LoginResponseDTO(token, user.getId(), user.getUsername(), user.getRole().name());
    }
}
