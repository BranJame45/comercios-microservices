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

## PostgreSQL avanzado en comercios-service

- **Bloqueo optimista (`@Version`)** en `ComercioEntity`: cada UPDATE compara
  la versión leída; si otra transacción ya modificó la fila, el segundo guardado
  lanza `OptimisticLockException` y el API responde `409 Conflict` para que el
  cliente recargue y reintente.
- **Transacción** en el caso de uso de cambio de estado (`@Transactional`):
  lectura, regla de negocio y escritura son atómicas.
- **Índice único sobre `ruc`**: las búsquedas por RUC son la consulta más
  frecuente (validación de duplicados al afiliar). PostgreSQL crea un índice
  B-tree para toda restricción UNIQUE; se declara con nombre estable
  (`idx_comercios_ruc`) para documentar el camino de acceso y mantenerlo
  portátil entre gestores.
- **Listado paginado con filtro por estado**, resuelto en la base de datos con
  Spring Data `Pageable`:

  ```
  GET /api/v1/comercios?estado=APROBADO&page=0&size=20
  ```

## Estado del avance

- [x] Fase 1: monorepo de microservicios + docker-compose local
- [x] Fase 2: PostgreSQL avanzado (bloqueo optimista, paginación)
- [ ] Fase 3: notificaciones-service con Gradle + RabbitMQ
- [ ] Fase 4: SonarCloud + Dockerfiles multi-stage
- [ ] Fase 5: despliegue en Kubernetes local (kind)
- [ ] Fase 6: documentación final y espejo en Bitbucket
