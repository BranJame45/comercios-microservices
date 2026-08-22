package com.brandonisla.notificaciones.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Repositorio Spring Data JPA (detalle de infraestructura). */
public interface NotificacionJpaRepository extends JpaRepository<NotificacionEntity, UUID> {
    List<NotificacionEntity> findAllByOrderByCreadoEnDesc();
}
