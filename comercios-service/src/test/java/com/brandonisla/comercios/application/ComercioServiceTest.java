package com.brandonisla.comercios.application;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import com.brandonisla.comercios.domain.port.ComercioRepository;
import com.brandonisla.comercios.domain.port.PublicadorEventos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de comercios con Mockito (patrón AAA),
 * sin levantar Spring ni base de datos: rápidas y enfocadas en la lógica.
 */
@ExtendWith(MockitoExtension.class)
class ComercioServiceTest {

    @Mock
    private ComercioRepository repositorio;

    @Mock
    private PublicadorEventos publicadorEventos;

    @InjectMocks
    private ComercioService servicio;

    @Test
    void afiliar_creaComercioEnEstadoPendiente() {
        // Arrange
        when(repositorio.existePorRuc("20123456789")).thenReturn(false);
        when(repositorio.guardar(any(Comercio.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Comercio c = servicio.afiliar("20123456789", "Mi Empresa SAC", "Mi Empresa", "Retail");

        // Assert
        assertThat(c.getEstado()).isEqualTo(EstadoAfiliacion.PENDIENTE);
        assertThat(c.getRuc()).isEqualTo("20123456789");
        verify(repositorio).guardar(any(Comercio.class));
    }

    @Test
    void afiliar_rucDuplicado_lanzaExcepcion() {
        when(repositorio.existePorRuc("20123456789")).thenReturn(true);

        assertThatThrownBy(() -> servicio.afiliar("20123456789", "X", "X", "X"))
                .isInstanceOf(RucDuplicadoException.class);
        verify(repositorio, never()).guardar(any());
    }

    @Test
    void obtener_inexistente_lanzaNoEncontrado() {
        UUID id = UUID.randomUUID();
        when(repositorio.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtener(id))
                .isInstanceOf(ComercioNoEncontradoException.class);
    }

    @Test
    void cambiarEstado_aprobado_actualizaYPublicaEvento() {
        UUID id = UUID.randomUUID();
        Comercio existente = Comercio.nuevo("20123456789", "Empresa", "Empresa", "Retail");
        existente.setId(id);
        when(repositorio.buscarPorId(id)).thenReturn(Optional.of(existente));
        when(repositorio.guardar(any(Comercio.class))).thenAnswer(inv -> inv.getArgument(0));

        Comercio actualizado = servicio.cambiarEstado(id, EstadoAfiliacion.APROBADO);

        assertThat(actualizado.getEstado()).isEqualTo(EstadoAfiliacion.APROBADO);
        verify(publicadorEventos).publicarComercioAprobado(actualizado);
    }

    @Test
    void cambiarEstado_suspendido_noPublicaEvento() {
        UUID id = UUID.randomUUID();
        Comercio existente = Comercio.nuevo("20123456789", "Empresa", "Empresa", "Retail");
        existente.setId(id);
        when(repositorio.buscarPorId(id)).thenReturn(Optional.of(existente));
        when(repositorio.guardar(any(Comercio.class))).thenAnswer(inv -> inv.getArgument(0));

        Comercio actualizado = servicio.cambiarEstado(id, EstadoAfiliacion.SUSPENDIDO);

        assertThat(actualizado.getEstado()).isEqualTo(EstadoAfiliacion.SUSPENDIDO);
        verify(publicadorEventos, never()).publicarComercioAprobado(any());
    }

    @Test
    void cambiarEstado_sobreComercioRechazado_lanzaReglaNegocio() {
        UUID id = UUID.randomUUID();
        Comercio rechazado = Comercio.nuevo("20123456789", "Empresa", "Empresa", "Retail");
        rechazado.setId(id);
        rechazado.setEstado(EstadoAfiliacion.RECHAZADO);
        when(repositorio.buscarPorId(id)).thenReturn(Optional.of(rechazado));

        assertThatThrownBy(() -> servicio.cambiarEstado(id, EstadoAfiliacion.APROBADO))
                .isInstanceOf(IllegalStateException.class);
    }
}
