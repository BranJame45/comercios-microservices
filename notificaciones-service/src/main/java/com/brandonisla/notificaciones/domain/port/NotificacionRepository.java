package com.brandonisla.notificaciones.domain.port;

import com.brandonisla.notificaciones.domain.model.Notificacion;

import java.util.List;

/**
 * Puerto de salida (hexagonal): el dominio define la interfaz y la
 * infraestructura la implementa (adaptador JPA).
 */
public interface NotificacionRepository {
    Notificacion guardar(Notificacion notificacion);
    List<Notificacion> listar();
}
