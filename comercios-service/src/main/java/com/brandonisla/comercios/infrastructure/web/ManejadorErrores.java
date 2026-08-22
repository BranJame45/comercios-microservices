package com.brandonisla.comercios.infrastructure.web;

import com.brandonisla.comercios.application.ComercioNoEncontradoException;
import com.brandonisla.comercios.application.RucDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/** Traduce las excepciones de dominio/aplicación a respuestas HTTP claras. */
@RestControllerAdvice
public class ManejadorErrores {

    private Map<String, Object> cuerpo(HttpStatus status, String mensaje) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", Instant.now().toString());
        m.put("status", status.value());
        m.put("error", status.getReasonPhrase());
        m.put("message", mensaje);
        return m;
    }

    @ExceptionHandler(ComercioNoEncontradoException.class)
    public ResponseEntity<Object> noEncontrado(ComercioNoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(RucDuplicadoException.class)
    public ResponseEntity<Object> rucDuplicado(RucDuplicadoException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo(HttpStatus.CONFLICT, e.getMessage()));
    }

    /**
     * Conflicto de bloqueo optimista: otra transacción actualizó el mismo
     * comercio primero. Se informa al cliente para que recargue y reintente.
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> conflictoConcurrencia(ObjectOptimisticLockingFailureException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo(HttpStatus.CONFLICT,
                "El comercio fue modificado por otro usuario. Recárgalo e inténtalo de nuevo."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> reglaNegocio(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(cuerpo(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> validacion(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst().orElse("Datos inválidos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo(HttpStatus.BAD_REQUEST, msg));
    }
}
