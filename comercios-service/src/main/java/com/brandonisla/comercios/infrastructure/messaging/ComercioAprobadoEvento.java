package com.brandonisla.comercios.infrastructure.messaging;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento "ComercioAprobado" publicado cuando un comercio pasa a APROBADO.
 * Viaja en JSON por RabbitMQ; notificaciones-service lo consume.
 */
public record ComercioAprobadoEvento(
        UUID comercioId,
        String ruc,
        String razonSocial,
        Instant aprobadoEn
) {
}
