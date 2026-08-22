package com.brandonisla.comercios.infrastructure.persistence;

import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Representación JPA del comercio (capa de infraestructura).
 *
 * El índice único sobre ruc se declara aquí con nombre estable porque las
 * búsquedas por RUC son la consulta más frecuente (validación de duplicados
 * al afiliar). PostgreSQL ya crea un índice B-tree para toda restricción
 * UNIQUE; declararlo explícito documenta el camino de acceso y lo hace
 * portátil entre gestores.
 */
@Entity
@Table(name = "comercios",
        uniqueConstraints = @UniqueConstraint(name = "uq_comercios_ruc", columnNames = "ruc"),
        indexes = @Index(name = "idx_comercios_ruc", columnList = "ruc", unique = true))
public class ComercioEntity {

    @Id
    private UUID id;

    @Column(nullable = false, length = 11)
    private String ruc;

    @Column(nullable = false)
    private String razonSocial;

    private String nombreComercial;
    private String rubro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAfiliacion estado;

    private Instant creadoEn;
    private Instant actualizadoEn;

    /**
     * Bloqueo optimista: Hibernate incrementa la versión en cada UPDATE y
     * la incluye en la cláusula WHERE. Si dos transacciones leen la misma
     * versión, la segunda recibe OptimisticLockException al confirmar.
     */
    @Version
    private Long version;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    public String getRubro() { return rubro; }
    public void setRubro(String rubro) { this.rubro = rubro; }
    public EstadoAfiliacion getEstado() { return estado; }
    public void setEstado(EstadoAfiliacion estado) { this.estado = estado; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
    public Instant getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(Instant actualizadoEn) { this.actualizadoEn = actualizadoEn; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
