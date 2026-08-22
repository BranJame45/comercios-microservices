# comercios-microservices

Sistema de microservicios para la afiliación de comercios. Parte de un
microservicio base (`comercios-service`) que gestiona la afiliación de
comercios y evoluciona hacia una arquitectura de microservicios con:

- **comercios-service** — afiliación y gestión de comercios (Spring Boot 3, Maven).
- **notificaciones-service** — consume eventos de aprobación vía RabbitMQ y
  registra notificaciones (Spring Boot 3, Gradle). *(En construcción, Fase 3)*
- **RabbitMQ** — mensajería asíncrona entre servicios.
- **PostgreSQL** — una base de datos por servicio (database-per-service).

## Estructura del proyecto

```
comercios-microservices/
├── comercios-service/        # Microservicio de afiliación (Maven)
├── notificaciones-service/   # Microservicio de notificaciones (Gradle) - Fase 3
├── k8s/                      # Manifiestos de Kubernetes - Fase 5
├── scripts/                  # Scripts de apoyo (init de bases de datos)
└── docker-compose.yml        # Infraestructura local: Postgres + RabbitMQ
```

## Levantar la infraestructura local

Requisitos: Docker Desktop instalado.

```bash
docker compose up -d
```

Esto levanta:

| Servicio  | Puerto | Credenciales demo            |
|-----------|--------|------------------------------|
| PostgreSQL | 5432  | `comercios` / `comercios`    |
| RabbitMQ management | 15672 (panel) · 5672 (AMQP) | `comercios` / `comercios` |

Bases de datos creadas: `comercios` y `notificaciones`.

## Correr comercios-service en desarrollo

```bash
cd comercios-service
mvn spring-boot:run
```

## Estado del avance

- [x] Fase 1: monorepo de microservicios + docker-compose local
- [ ] Fase 2: PostgreSQL avanzado (bloqueo optimista, paginación)
- [ ] Fase 3: notificaciones-service con Gradle + RabbitMQ
- [ ] Fase 4: SonarCloud + Dockerfiles multi-stage
- [ ] Fase 5: despliegue en Kubernetes local (kind)
- [ ] Fase 6: documentación final y espejo en Bitbucket
