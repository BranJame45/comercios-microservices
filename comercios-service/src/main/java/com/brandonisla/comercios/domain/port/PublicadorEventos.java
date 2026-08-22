package com.brandonisla.comercios.domain.port;

import com.brandonisla.comercios.domain.model.Comercio;

/**
 * Puerto de salida para publicar eventos de dominio a la mensajería.
 * La infraestructura lo implementa con RabbitMQ.
 */
public interface PublicadorEventos {
    void publicarComercioAprobado(Comercio comercio);
}
