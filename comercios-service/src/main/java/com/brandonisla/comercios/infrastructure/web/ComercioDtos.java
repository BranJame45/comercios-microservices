package com.brandonisla.comercios.infrastructure.web;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.UUID;

/** DTOs de entrada/salida del API de comercios. */
public class ComercioDtos {

    public record CrearComercioRequest(
            @NotBlank @Pattern(regexp = "\\d{11}", message = "El RUC debe tener 11 dígitos") String ruc,
            @NotBlank String razonSocial,
            String nombreComercial,
            String rubro
    ) {}

    public record CambiarEstadoRequest(
            @NotNull EstadoAfiliacion estado
    ) {}

    public record ComercioResponse(
            UUID id, String ruc, String razonSocial, String nombreComercial,
            String rubro, EstadoAfiliacion estado, Instant creadoEn, Instant actualizadoEn
    ) {
        public static ComercioResponse desde(Comercio c) {
            return new ComercioResponse(c.getId(), c.getRuc(), c.getRazonSocial(),
                    c.getNombreComercial(), c.getRubro(), c.getEstado(), c.getCreadoEn(), c.getActualizadoEn());
        }
    }
}
