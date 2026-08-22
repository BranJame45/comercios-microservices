package com.brandonisla.notificaciones.application;

import com.brandonisla.notificaciones.domain.model.Notificacion;
import com.brandonisla.notificaciones.domain.port.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de notificaciones con Mockito (patrón AAA),
 * sin levantar Spring ni mensajería: rápidas y enfocadas en la lógica.
 */
@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository repositorio;

    @InjectMocks
    private NotificacionService servicio;

    @Test
    void registrarAprobacion_creaNotificacionDelComercio() {
        // Arrange
        UUID comercioId = UUID.randomUUID();
        Instant aprobadoEn = Instant.now();
        when(repositorio.guardar(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        ComercioAprobadoEvento evento = new ComercioAprobadoEvento(
                comercioId, "20123456789", "Mi Empresa SAC", aprobadoEn);

        // Act
        Notificacion n = servicio.registrarAprobacion(evento);

        // Assert
        assertThat(n.getComercioId()).isEqualTo(comercioId);
        assertThat(n.getTipo()).isEqualTo("COMERCIO_APROBADO");
        assertThat(n.getMensaje()).contains("Mi Empresa SAC");
        assertThat(n.getCreadoEn()).isEqualTo(aprobadoEn);
    }

    @Test
    void registrarAprobacion_sinFechaUsaMomentoActual() {
        // Arrange
        when(repositorio.guardar(any(Notificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        ComercioAprobadoEvento evento = new ComercioAprobadoEvento(
                UUID.randomUUID(), "20123456789", "Empresa", null);

        // Act
        Notificacion n = servicio.registrarAprobacion(evento);

        // Assert: la notificación no puede quedar sin fecha de creación
        ArgumentCaptor<Notificacion> captura = ArgumentCaptor.forClass(Notificacion.class);
        verify(repositorio).guardar(captura.capture());
        assertThat(captura.getValue().getCreadoEn()).isNotNull();
        assertThat(n.getCreadoEn()).isNotNull();
    }
}
