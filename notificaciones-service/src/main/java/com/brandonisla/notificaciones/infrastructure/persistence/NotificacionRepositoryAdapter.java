package com.brandonisla.notificaciones.infrastructure.persistence;

import com.brandonisla.notificaciones.domain.model.Notificacion;
import com.brandonisla.notificaciones.domain.port.NotificacionRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Adaptador: traduce entre el dominio (Notificacion) y la persistencia
 * (NotificacionEntity), implementando el puerto NotificacionRepository.
 */
@Component
public class NotificacionRepositoryAdapter implements NotificacionRepository {

    private final NotificacionJpaRepository jpa;

    public NotificacionRepositoryAdapter(NotificacionJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Notificacion guardar(Notificacion n) {
        NotificacionEntity e = new NotificacionEntity();
        e.setId(n.getId());
        e.setComercioId(n.getComercioId());
        e.setMensaje(n.getMensaje());
        e.setTipo(n.getTipo());
        e.setCreadoEn(n.getCreadoEn());
        return aDominio(jpa.save(e));
    }

    @Override
    public List<Notificacion> listar() {
        return jpa.findAllByOrderByCreadoEnDesc().stream().map(this::aDominio).toList();
    }

    private Notificacion aDominio(NotificacionEntity e) {
        return new Notificacion(e.getId(), e.getComercioId(), e.getMensaje(), e.getTipo(), e.getCreadoEn());
    }
}
