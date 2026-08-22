package com.brandonisla.notificaciones.application;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento "ComercioAprobado" publicado por comercios-service vía RabbitMQ.
 * Es un contrato de mensajería: se duplica aquí a propósito, sin acoplarse
 * al código del otro microservicio.
 */
public record ComercioAprobadoEvento(
        UUID comercioId,
        String ruc,
        String razonSocial,
        Instant aprobadoEn
) {
}
