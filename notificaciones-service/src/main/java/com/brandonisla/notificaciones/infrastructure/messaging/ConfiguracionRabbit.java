package com.brandonisla.notificaciones.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Topología de mensajería: el intercambio lo publica comercios-service y
 * este microservicio declara su cola y enlace (operaciones idempotentes).
 */
@Configuration
public class ConfiguracionRabbit {

    public static final String INTERCAMBIO_COMERCIOS = "comercios.eventos";
    public static final String COLA_APROBADOS = "notificaciones.comercio-aprobado";
    public static final String CLAVE_APROBADO = "comercio.aprobado";

    @Bean
    public TopicExchange intercambioComercios() {
        return new TopicExchange(INTERCAMBIO_COMERCIOS, true, false);
    }

    @Bean
    public Queue colaAprobados() {
        return QueueBuilder.durable(COLA_APROBADOS).build();
    }

    @Bean
    public Binding enlaceAprobados(Queue colaAprobados, TopicExchange intercambioComercios) {
        return BindingBuilder.bind(colaAprobados).to(intercambioComercios).with(CLAVE_APROBADO);
    }

    /**
     * Los eventos viajan en JSON para desacoplar productor y consumidor.
     * Con precedencia INFERRED, el tipo de destino se deduce del parámetro
     * del listener y no del encabezado __TypeId__ del productor, evitando
     * acoplarse a los nombres de paquetes ajenos.
     */
    @Bean
    public MessageConverter convertidorJson() {
        Jackson2JsonMessageConverter convertidor = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper mapeador = new DefaultJackson2JavaTypeMapper();
        mapeador.setTrustedPackages("com.brandonisla");
        mapeador.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        convertidor.setJavaTypeMapper(mapeador);
        return convertidor;
    }
}
