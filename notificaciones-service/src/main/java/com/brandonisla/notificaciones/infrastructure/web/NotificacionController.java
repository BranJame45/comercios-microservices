package com.brandonisla.notificaciones.infrastructure.web;

import com.brandonisla.notificaciones.application.NotificacionService;
import com.brandonisla.notificaciones.infrastructure.web.NotificacionDtos.NotificacionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@Tag(name = "Notificaciones", description = "Notificaciones generadas por eventos de comercios")
public class NotificacionController {

    private final NotificacionService servicio;

    public NotificacionController(NotificacionService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    @Operation(summary = "Listar las notificaciones generadas")
    public List<NotificacionResponse> listar() {
        return NotificacionDtos.desdeLista(servicio.listar());
    }
}
