package com.brandonisla.comercios.infrastructure.persistence;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import com.brandonisla.comercios.domain.model.Pagina;
import com.brandonisla.comercios.domain.port.ComercioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador: traduce entre el dominio (Comercio) y la persistencia
 * (ComercioEntity), implementando el puerto ComercioRepository.
 */
@Component
public class ComercioRepositoryAdapter implements ComercioRepository {

    private final ComercioJpaRepository jpa;

    public ComercioRepositoryAdapter(ComercioJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Comercio guardar(Comercio comercio) {
        return aDominio(jpa.save(aEntidad(comercio)));
    }

    @Override
    public Optional<Comercio> buscarPorId(UUID id) {
        return jpa.findById(id).map(this::aDominio);
    }

    @Override
    public List<Comercio> listar() {
        return jpa.findAll().stream().map(this::aDominio).toList();
    }

    @Override
    public Pagina<Comercio> listar(EstadoAfiliacion estado, int pagina, int tamanio) {
        // La paginación y el filtro se resuelven en la base de datos (Pageable),
        // no en memoria.
        Page<ComercioEntity> resultado = (estado == null)
                ? jpa.findAll(PageRequest.of(pagina, tamanio))
                : jpa.findByEstado(estado, PageRequest.of(pagina, tamanio));
        List<Comercio> contenido = resultado.getContent().stream().map(this::aDominio).toList();
        return new Pagina<>(contenido, resultado.getTotalElements(), resultado.getTotalPages(),
                resultado.getNumber(), resultado.getSize());
    }

    @Override
    public boolean existePorRuc(String ruc) {
        return jpa.existsByRuc(ruc);
    }

    @Override
    public void eliminar(UUID id) {
        jpa.deleteById(id);
    }

    private ComercioEntity aEntidad(Comercio c) {
        ComercioEntity e = new ComercioEntity();
        e.setId(c.getId());
        e.setRuc(c.getRuc());
        e.setRazonSocial(c.getRazonSocial());
        e.setNombreComercial(c.getNombreComercial());
        e.setRubro(c.getRubro());
        e.setEstado(c.getEstado());
        e.setCreadoEn(c.getCreadoEn());
        e.setActualizadoEn(c.getActualizadoEn());
        e.setVersion(c.getVersion());
        return e;
    }

    private Comercio aDominio(ComercioEntity e) {
        return new Comercio(e.getId(), e.getRuc(), e.getRazonSocial(), e.getNombreComercial(),
                e.getRubro(), e.getEstado(), e.getCreadoEn(), e.getActualizadoEn(), e.getVersion());
    }
}
