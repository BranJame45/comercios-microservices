package com.brandonisla.notificaciones.application;

import com.brandonisla.notificaciones.domain.model.Notificacion;
import com.brandonisla.notificaciones.domain.port.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Casos de uso de notificaciones. Registra notificaciones a partir de los
 * eventos que llegan por RabbitMQ y las expone para consulta.
 */
@Service
public class NotificacionService {

    private final NotificacionRepository repositorio;

    public NotificacionService(NotificacionRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Notificacion registrarAprobacion(ComercioAprobadoEvento evento) {
        Instant cuando = evento.aprobadoEn() != null ? evento.aprobadoEn() : Instant.now();
        return repositorio.guardar(
                Notificacion.deAprobacion(evento.comercioId(), evento.razonSocial(), cuando));
    }

    public List<Notificacion> listar() {
        return repositorio.listar();
    }
}
