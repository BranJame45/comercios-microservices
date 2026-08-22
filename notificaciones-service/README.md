# notificaciones-service

Microservicio que consume el evento `ComercioAprobado` publicado por
comercios-service vía RabbitMQ y registra una notificación por cada evento en
su propia base de datos PostgreSQL (`notificaciones`).

Construido con Spring Boot 3 (Java 21) y **Gradle**, misma arquitectura
hexagonal del proyecto: `domain` / `application` / `infrastructure`.

## Correr

Requiere la infraestructura local (`docker compose up` desde la raíz).

```bash
gradlew.bat bootRun        # Linux/Mac: ./gradlew bootRun
```

Puerto: **8081**

## Endpoints

| Método | Ruta                     | Descripción                          |
|--------|--------------------------|--------------------------------------|
| GET    | `/api/v1/notificaciones` | Lista las notificaciones registradas |
| GET    | `/salud`                 | Verificación de vida                 |

## Mensajería

- Intercambio: `comercios.eventos` (topic)
- Cola: `notificaciones.comercio-aprobado`
- Clave de ruteo: `comercio.aprobado`

Los eventos viajan en JSON; el tipo de destino se deduce del parámetro del
listener para no acoplarse a los paquetes del productor.

## Pruebas

```bash
gradlew.bat test
```

Pruebas unitarias del servicio de aplicación con JUnit 5 + Mockito (patrón AAA).
