package com.brandonisla.notificaciones.infrastructure.web;

import com.brandonisla.notificaciones.domain.model.Notificacion;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** DTOs de salida del API de notificaciones. */
public class NotificacionDtos {

    public record NotificacionResponse(
            UUID id, UUID comercioId, String mensaje, String tipo, Instant creadoEn
    ) {
        public static NotificacionResponse desde(Notificacion n) {
            return new NotificacionResponse(n.getId(), n.getComercioId(), n.getMensaje(),
                    n.getTipo(), n.getCreadoEn());
        }
    }

    public static List<NotificacionResponse> desdeLista(List<Notificacion> notificaciones) {
        return notificaciones.stream().map(NotificacionResponse::desde).toList();
    }
}
