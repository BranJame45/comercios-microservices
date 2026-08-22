package com.brandonisla.comercios.infrastructure.persistence;

import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifica el bloqueo optimista (@Version) contra una base real (H2 en
 * memoria): dos sesiones leen el mismo comercio; la primera actualiza y
 * confirma, y la segunda —que trabajó con una versión ya obsoleta— debe
 * fallar al intentar guardar.
 */
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class ComercioEntityBloqueoOptimistaTest {

    @Autowired
    private ComercioJpaRepository repositorio;

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    private ComercioEntity nuevoComercio(String ruc) {
        Instant ahora = Instant.now();
        ComercioEntity e = new ComercioEntity();
        e.setId(UUID.randomUUID());
        e.setRuc(ruc);
        e.setRazonSocial("Empresa Demo SAC");
        e.setNombreComercial("Empresa Demo");
        e.setRubro("Retail");
        e.setEstado(EstadoAfiliacion.PENDIENTE);
        e.setCreadoEn(ahora);
        e.setActualizadoEn(ahora);
        return e;
    }

    @Test
    void actualizacionConcurrente_lanzaOptimisticLockException() {
        // Arrange: un comercio persistido leído por dos sesiones distintas
        UUID id = repositorio.saveAndFlush(nuevoComercio("20111111111")).getId();
        ComercioEntity sesionUno = repositorio.findById(id).orElseThrow();
        ComercioEntity sesionDos = repositorio.findById(id).orElseThrow();

        // Act: la primera sesión aprueba el comercio y confirma su cambio
        sesionUno.setEstado(EstadoAfiliacion.APROBADO);
        ComercioEntity confirmado = repositorio.save(sesionUno);

        // Assert: la segunda sesión tenía la versión vieja y su guardado choca
        assertThat(confirmado.getVersion()).isEqualTo(1L);
        sesionDos.setEstado(EstadoAfiliacion.SUSPENDIDO);
        assertThatThrownBy(() -> repositorio.save(sesionDos))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    void version_seIncrementaEnCadaActualizacion() {
        // Arrange
        UUID id = repositorio.saveAndFlush(nuevoComercio("20222222222")).getId();

        // Act: tres actualizaciones secuenciales sobre lecturas frescas
        long ultimaVersion = 0;
        for (int i = 1; i <= 3; i++) {
            ComercioEntity comercio = repositorio.findById(id).orElseThrow();
            assertThat(comercio.getVersion()).isEqualTo(ultimaVersion);
            comercio.setRubro("Rubro " + i);
            ultimaVersion = repositorio.save(comercio).getVersion();
        }

        // Assert: cada UPDATE incrementó exactamente en uno la versión
        assertThat(ultimaVersion).isEqualTo(3L);
    }
}
