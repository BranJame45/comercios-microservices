package com.brandonisla.comercios.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología de mensajería: el intercambio se declara aquí (operación
 * idempotente) y la cola la declara el consumidor.
 */
@Configuration
public class ConfiguracionRabbit {

    public static final String INTERCAMBIO_COMERCIOS = "comercios.eventos";
    public static final String CLAVE_APROBADO = "comercio.aprobado";

    @Bean
    public TopicExchange intercambioComercios() {
        return new TopicExchange(INTERCAMBIO_COMERCIOS, true, false);
    }

    /** Los eventos viajan en JSON para desacoplar productor y consumidor. */
    @Bean
    public MessageConverter convertidorJson() {
        return new Jackson2JsonMessageConverter();
    }
}
