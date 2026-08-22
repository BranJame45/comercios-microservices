package com.brandonisla.comercios.application;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import com.brandonisla.comercios.domain.port.ComercioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Casos de uso de afiliación de comercios. Orquesta el dominio y el
 * repositorio (puerto), sin conocer detalles de web ni de persistencia.
 */
@Service
public class ComercioService {

    private final ComercioRepository repositorio;

    public ComercioService(ComercioRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Comercio afiliar(String ruc, String razonSocial, String nombreComercial, String rubro) {
        if (repositorio.existePorRuc(ruc)) {
            throw new RucDuplicadoException("Ya existe un comercio con el RUC " + ruc);
        }
        Comercio comercio = Comercio.nuevo(ruc, razonSocial, nombreComercial, rubro);
        return repositorio.guardar(comercio);
    }

    public List<Comercio> listar() {
        return repositorio.listar();
    }

    public Comercio obtener(UUID id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ComercioNoEncontradoException("Comercio no encontrado: " + id));
    }

    public Comercio cambiarEstado(UUID id, EstadoAfiliacion nuevoEstado) {
        Comercio comercio = obtener(id);
        comercio.cambiarEstado(nuevoEstado); // regla de negocio vive en el dominio
        return repositorio.guardar(comercio);
    }

    public void eliminar(UUID id) {
        obtener(id); // valida existencia
        repositorio.eliminar(id);
    }
}
