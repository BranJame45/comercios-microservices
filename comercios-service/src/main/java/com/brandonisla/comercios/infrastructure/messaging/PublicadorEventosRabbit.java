package com.brandonisla.comercios.infrastructure.messaging;

import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.port.PublicadorEventos;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

/** Adaptador RabbitMQ del puerto PublicadorEventos. */
@Component
public class PublicadorEventosRabbit implements PublicadorEventos {

    private final RabbitTemplate rabbit;

    public PublicadorEventosRabbit(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
    }

    @Override
    public void publicarComercioAprobado(Comercio comercio) {
        ComercioAprobadoEvento evento = new ComercioAprobadoEvento(
                comercio.getId(), comercio.getRuc(), comercio.getRazonSocial(), Instant.now());
        // El intercambio usa Jackson (convertidorJson) para serializar a JSON.
        rabbit.convertAndSend(ConfiguracionRabbit.INTERCAMBIO_COMERCIOS,
                ConfiguracionRabbit.CLAVE_APROBADO, evento);
    }
}
