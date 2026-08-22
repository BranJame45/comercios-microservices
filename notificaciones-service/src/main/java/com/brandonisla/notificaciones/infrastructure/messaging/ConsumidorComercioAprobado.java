package com.brandonisla.notificaciones.infrastructure.messaging;

import com.brandonisla.notificaciones.application.ComercioAprobadoEvento;
import com.brandonisla.notificaciones.application.NotificacionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor del evento "ComercioAprobado": por cada evento genera una
 * notificación persistida en la base propia de este servicio.
 */
@Component
public class ConsumidorComercioAprobado {

    private final NotificacionService servicio;

    public ConsumidorComercioAprobado(NotificacionService servicio) {
        this.servicio = servicio;
    }

    @RabbitListener(queues = ConfiguracionRabbit.COLA_APROBADOS)
    public void recibir(ComercioAprobadoEvento evento) {
        servicio.registrarAprobacion(evento);
    }
}
