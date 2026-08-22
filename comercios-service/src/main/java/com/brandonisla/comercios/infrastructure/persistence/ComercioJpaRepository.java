package com.brandonisla.comercios.infrastructure.persistence;

import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Repositorio Spring Data JPA (detalle de infraestructura). */
public interface ComercioJpaRepository extends JpaRepository<ComercioEntity, UUID> {
    boolean existsByRuc(String ruc);
    Page<ComercioEntity> findByEstado(EstadoAfiliacion estado, Pageable pageable);
}
