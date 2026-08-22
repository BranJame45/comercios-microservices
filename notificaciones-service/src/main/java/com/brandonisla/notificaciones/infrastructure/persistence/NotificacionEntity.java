package com.brandonisla.notificaciones.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/** Representación JPA de la notificación (capa de infraestructura). */
@Entity
@Table(name = "notificaciones")
public class NotificacionEntity {

    @Id
    private UUID id;

    @Column(name = "comercio_id", nullable = false)
    private UUID comercioId;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false, length = 50)
    private String tipo;

    private Instant creadoEn;

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
