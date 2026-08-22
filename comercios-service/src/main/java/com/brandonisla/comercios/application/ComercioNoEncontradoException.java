package com.brandonisla.comercios.application;

/** Se lanza cuando no existe un comercio con el id solicitado. */
public class ComercioNoEncontradoException extends RuntimeException {
    public ComercioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
