package com.brandonisla.comercios.domain.model;

import java.util.List;

/**
 * Página de resultados del dominio, independiente de Spring Data.
 * La infraestructura la llena a partir de su propia paginación.
 */
public record Pagina<T>(
        List<T> contenido,
        long totalElementos,
        int totalPaginas,
        int numeroPagina,
        int tamanioPagina
) {
}
