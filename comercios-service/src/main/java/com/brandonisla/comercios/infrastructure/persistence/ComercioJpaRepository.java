package com.brandonisla.comercios.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Repositorio Spring Data JPA (detalle de infraestructura). */
public interface ComercioJpaRepository extends JpaRepository<ComercioEntity, UUID> {
    boolean existsByRuc(String ruc);
}
