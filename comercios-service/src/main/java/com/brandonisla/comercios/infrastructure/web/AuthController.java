package com.brandonisla.comercios.infrastructure.web;

import com.brandonisla.comercios.infrastructure.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Autenticación y emisión de tokens JWT")
public class AuthController {

    private final JwtService jwtService;
    private final String usuarioAdmin;
    private final String claveAdmin;

    public AuthController(JwtService jwtService,
                         @Value("${app.admin.username}") String usuarioAdmin,
                         @Value("${app.admin.password}") String claveAdmin) {
        this.jwtService = jwtService;
        this.usuarioAdmin = usuarioAdmin;
        this.claveAdmin = claveAdmin;
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record LoginResponse(String accessToken, String tokenType) {}

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión y devuelve un token JWT")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        if (usuarioAdmin.equals(req.username()) && claveAdmin.equals(req.password())) {
            String token = jwtService.generar(req.username());
            return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }
}
