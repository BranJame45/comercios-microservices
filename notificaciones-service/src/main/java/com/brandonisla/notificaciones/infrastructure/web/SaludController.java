package com.brandonisla.notificaciones.infrastructure.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/** Punto de verificación de vida del servicio (para probes y monitoreo). */
@RestController
public class SaludController {

    @GetMapping("/salud")
    public Map<String, Object> salud() {
        return Map.of("estado", "UP", "timestamp", Instant.now().toString());
    }
}
