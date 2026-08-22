package com.brandonisla.notificaciones.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Entidad de dominio: una notificación generada por un evento de comercios.
 * Sin dependencias de framework (JPA/Spring viven en la infraestructura).
 */
public class Notificacion {

    private UUID id;
    private UUID comercioId;
    private String mensaje;
    private String tipo;
    private Instant creadoEn;

    public Notificacion() {
    }

    public Notificacion(UUID id, UUID comercioId, String mensaje, String tipo, Instant creadoEn) {
        this.id = id;
        this.comercioId = comercioId;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.creadoEn = creadoEn;
    }

    /** Regla de negocio: una aprobación de comercio genera su notificación. */
    public static Notificacion deAprobacion(UUID comercioId, String razonSocial, Instant cuando) {
        return new Notificacion(
                UUID.randomUUID(),
                comercioId,
                "El comercio \"" + razonSocial + "\" fue aprobado",
                "COMERCIO_APROBADO",
                cuando);
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getComercioId() { return comercioId; }
    public void setComercioId(UUID comercioId) { this.comercioId = comercioId; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Instant getCreadoEn() { return creadoEn; }
    public void setCreadoEn(Instant creadoEn) { this.creadoEn = creadoEn; }
}
