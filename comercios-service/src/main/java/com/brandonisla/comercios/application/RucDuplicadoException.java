package com.brandonisla.comercios.application;

/** Se lanza al intentar afiliar un comercio con un RUC ya registrado. */
public class RucDuplicadoException extends RuntimeException {
    public RucDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
