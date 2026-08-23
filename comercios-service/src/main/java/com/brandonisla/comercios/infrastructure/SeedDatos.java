package com.brandonisla.comercios.infrastructure;

import com.brandonisla.comercios.application.ComercioService;
import com.brandonisla.comercios.domain.model.Comercio;
import com.brandonisla.comercios.domain.model.EstadoAfiliacion;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Carga datos de ejemplo al iniciar, solo si no hay comercios registrados.
 * Aprueba algunos comercios para que también se generen notificaciones
 * (a través del evento publicado en RabbitMQ).
 */
@Component
public class SeedDatos implements CommandLineRunner {

    private final ComercioService servicio;

    public SeedDatos(ComercioService servicio) {
        this.servicio = servicio;
    }

    @Override
    public void run(String... args) {
        if (!servicio.listar().isEmpty()) {
            return; // ya hay datos, no duplicar
        }

        // (ruc, razón social, nombre comercial, rubro, estado final)
        Object[][] comercios = {
            {"20100011111", "Comercial Los Andes S.A.C.", "Bodega Los Andes", "Retail", EstadoAfiliacion.APROBADO},
            {"20100022222", "Inversiones El Sol E.I.R.L.", "Minimarket El Sol", "Retail", EstadoAfiliacion.APROBADO},
            {"20100033333", "Restaurante Sabor Peruano S.A.C.", "Sabor Peruano", "Gastronomía", EstadoAfiliacion.APROBADO},
            {"20100044444", "Farmacia Vida Sana S.R.L.", "Botica Vida Sana", "Salud", EstadoAfiliacion.PENDIENTE},
            {"20100055555", "Servicios Digitales Lima S.A.C.", "TecnoLima", "Tecnología", EstadoAfiliacion.PENDIENTE},
            {"20100066666", "Transportes Rápidos del Sur E.I.R.L.", "Rápidos del Sur", "Transporte", EstadoAfiliacion.SUSPENDIDO},
        };

        for (Object[] c : comercios) {
            Comercio creado = servicio.afiliar((String) c[0], (String) c[1], (String) c[2], (String) c[3]);
            EstadoAfiliacion estadoFinal = (EstadoAfiliacion) c[4];
            if (estadoFinal != EstadoAfiliacion.PENDIENTE) {
                servicio.cambiarEstado(creado.getId(), estadoFinal);
            }
        }
    }
}
