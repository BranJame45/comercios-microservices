package com.brandonisla.comercios.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio: un comercio afiliado. Sin dependencias de framework
 * (JPA/Spring viven en la capa de infraestructura).
 */
public class Comercio {

    private UUID id;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String rubro;
    private EstadoAfiliacion estado;
    private Instant creadoEn;
    private Instant actualizadoEn;

    public Comercio() {
    }

    public Comercio(UUID id, String ruc, String razonSocial, String nombreComercial,
                    String rubro, EstadoAfiliacion estado, Instant creadoEn, Instant actualizadoEn) {
        this.id = id;
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.nombreComercial = nombreComercial;
        this.rubro = rubro;
        this.estado = estado;
        this.creadoEn = creadoEn;
        this.actualizadoEn = actualizadoEn;
    }

    /** Regla de negocio: un comercio nace en estado PENDIENTE. */
    public static Comercio nuevo(String ruc, String razonSocial, String nombreComercial, String rubro) {
        Instant ahora = Instant.now();
        return new Comercio(UUID.randomUUID(), ruc, razonSocial, nombreComercial, rubro,
                EstadoAfiliacion.PENDIENTE, ahora, ahora);
    }

    /** Regla de negocio: no se puede cambiar el estado de un comercio rechazado. */
    public void cambiarEstado(EstadoAfiliacion nuevoEstado) {
        if (this.estado == EstadoAfiliacion.RECHAZADO) {
            throw new IllegalStateException("Un comercio rechazado no puede cambiar de estado");
        }
        this.estado = nuevoEstado;
        this.actualizadoEn = Instant.now();
    }

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
