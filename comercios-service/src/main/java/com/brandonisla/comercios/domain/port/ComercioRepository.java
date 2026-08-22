package com.brandonisla.comercios.domain.port;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import com.brandonisla.comercios.domain.model.Pagina;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (hexagonal): el dominio define la interfaz y la
 * infraestructura la implementa (adaptador JPA).
 */
public interface ComercioRepository {
    Comercio guardar(Comercio comercio);
    Optional<Comercio> buscarPorId(UUID id);
    List<Comercio> listar();
    Pagina<Comercio> listar(EstadoAfiliacion estado, int pagina, int tamanio);
    boolean existePorRuc(String ruc);
    void eliminar(UUID id);
}
