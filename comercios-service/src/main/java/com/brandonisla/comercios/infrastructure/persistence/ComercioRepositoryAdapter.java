package com.brandonisla.comercios.infrastructure.persistence;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.port.ComercioRepository;
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
        return e;
    }

    private Comercio aDominio(ComercioEntity e) {
        return new Comercio(e.getId(), e.getRuc(), e.getRazonSocial(), e.getNombreComercial(),
                e.getRubro(), e.getEstado(), e.getCreadoEn(), e.getActualizadoEn());
    }
}
