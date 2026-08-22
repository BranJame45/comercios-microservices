package com.brandonisla.comercios.infrastructure.persistence;

import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Representación JPA del comercio (capa de infraestructura). */
@Entity
@Table(name = "comercios")
public class ComercioEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 11)
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
}
