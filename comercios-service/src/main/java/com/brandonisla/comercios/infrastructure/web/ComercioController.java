package com.brandonisla.comercios.infrastructure.web;

import com.brandonisla.comercios.application.ComercioService;
import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import com.brandonisla.comercios.infrastructure.web.ComercioDtos.CambiarEstadoRequest;
import com.brandonisla.comercios.infrastructure.web.ComercioDtos.ComercioResponse;
import com.brandonisla.comercios.infrastructure.web.ComercioDtos.CrearComercioRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/comercios")
@Tag(name = "Comercios", description = "Afiliación y gestión de comercios")
@SecurityRequirement(name = "bearerAuth")
public class ComercioController {

    private final ComercioService servicio;

    public ComercioController(ComercioService servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @Operation(summary = "Afiliar un nuevo comercio (nace en estado PENDIENTE)")
    public ResponseEntity<ComercioResponse> crear(@Valid @RequestBody CrearComercioRequest req) {
        Comercio c = servicio.afiliar(req.ruc(), req.razonSocial(), req.nombreComercial(), req.rubro());
        return ResponseEntity.created(URI.create("/api/v1/comercios/" + c.getId()))
                .body(ComercioResponse.desde(c));
    }

    @GetMapping
    @Operation(summary = "Listar comercios con filtro opcional por estado y paginación")
    public ComercioDtos.PaginaComerciosResponse listar(
            @RequestParam(required = false) EstadoAfiliacion estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ComercioDtos.PaginaComerciosResponse.desde(servicio.listar(estado, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un comercio por ID")
    public ComercioResponse obtener(@PathVariable UUID id) {
        return ComercioResponse.desde(servicio.obtener(id));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de afiliación de un comercio")
    public ComercioResponse cambiarEstado(@PathVariable UUID id, @Valid @RequestBody CambiarEstadoRequest req) {
        return ComercioResponse.desde(servicio.cambiarEstado(id, req.estado()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un comercio")
    public void eliminar(@PathVariable UUID id) {
        servicio.eliminar(id);
    }
}
