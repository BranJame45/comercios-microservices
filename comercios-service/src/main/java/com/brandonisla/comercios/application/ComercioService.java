package com.brandonisla.comercios.application;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import com.brandonisla.comercios.domain.model.Pagina;
import com.brandonisla.comercios.domain.port.ComercioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /** Listado paginado con filtro opcional por estado; se resuelve en la BD. */
    public Pagina<Comercio> listar(EstadoAfiliacion estado, int pagina, int tamanio) {
        return repositorio.listar(estado, pagina, tamanio);
    }

    public Comercio obtener(UUID id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ComercioNoEncontradoException("Comercio no encontrado: " + id));
    }

    /**
     * Cambio de estado dentro de una transacción: la lectura, la regla de
     * negocio y la escritura ocurren de forma atómica y el bloqueo optimista
     * (@Version) protege contra actualizaciones concurrentes.
     */
    @Transactional
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
