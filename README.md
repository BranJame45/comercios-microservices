# comercios-microservices

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/quality_gate?project=comercios-microservices)](https://sonarcloud.io/summary/new_code?id=comercios-microservices)

Sistema de microservicios para la afiliación de comercios. Parte de un
microservicio base (`comercios-service`) que gestiona la afiliación de
comercios y evoluciona hacia una arquitectura de microservicios con:

- **comercios-service** — afiliación y gestión de comercios (Spring Boot 3, Maven).
- **notificaciones-service** — consume eventos de aprobación vía RabbitMQ y
  registra notificaciones (Spring Boot 3, Gradle).
- **RabbitMQ** — mensajería asíncrona entre servicios.
- **PostgreSQL** — una base de datos por servicio (database-per-service).

Flujo principal: al aprobar un comercio, `comercios-service` publica el evento
`ComercioAprobado` en RabbitMQ; `notificaciones-service` lo consume y guarda la
notificación en su propia base de datos.

## Demo en vivo (AWS EC2)

Desplegado en AWS (imágenes en ECR ejecutándose en EC2 con Docker):

- **Swagger (comercios-service):** <http://3.14.217.168:8080/swagger-ui/index.html>
- **API notificaciones:** <http://3.14.217.168:8081/api/v1/notificaciones>

**Credenciales de prueba:** usuario `admin` · contraseña `Admin2026!`

Para probarlo: en Swagger usa `POST /api/v1/auth/login` con esas credenciales,
copia el `accessToken`, púlsalo en **Authorize** y ya puedes consumir los
endpoints protegidos. La base ya viene con comercios de ejemplo.

## Arquitectura

```
                   ┌──────────────────────┐
   Cliente  ──►    │  comercios-service   │  (Maven)  Postgres: comercios
                   │  (afiliación CRUD)   │
                   └───────────┬──────────┘
                               │  publica evento "ComercioAprobado"
                               ▼  (RabbitMQ)
                   ┌──────────────────────┐
                   │ notificaciones-service│ (Gradle) Postgres: notificaciones
                   │ (consume eventos,     │
                   │  registra notif.)     │
                   └──────────────────────┘
   Todo desplegable en Kubernetes local (kind) con los manifiestos de k8s/.
```

Ambos servicios siguen **arquitectura hexagonal** (`domain` / `application` /
`infrastructure`) y el patrón **database-per-service**.

## Stack

| Componente | Versión / herramienta |
|------------|----------------------|
| Java | 21 |
| Spring Boot | 3.3.x |
| comercios-service | Maven |
| notificaciones-service | Gradle |
| PostgreSQL | 16 (compose) · 15+ compatible |
| RabbitMQ | 3.x con management |
| Kubernetes local | kind |
| Calidad de código | SonarCloud + GitHub Actions |
| Pruebas | JUnit 5 + Mockito (patrón AAA) |

## Endpoints principales

**comercios-service** (puerto 8080, requiere JWT):

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/v1/auth/login` | Emite token JWT (admin demo) |
| POST | `/api/v1/comercios` | Afiliar comercio (nace PENDIENTE) |
| GET | `/api/v1/comercios?estado=&page=&size=` | Listar con filtro y paginación |
| GET | `/api/v1/comercios/{id}` | Obtener un comercio |
| PATCH | `/api/v1/comercios/{id}/estado` | Cambiar estado (dispara evento al aprobar) |
| DELETE | `/api/v1/comercios/{id}` | Eliminar comercio |

**notificaciones-service** (puerto 8081, sin autenticación):

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/v1/notificaciones` | Lista las notificaciones generadas |

## Credenciales demo

| Uso | Usuario | Contraseña |
|-----|---------|------------|
| Login API (comercios) | `admin` | `Admin2026!` |
| PostgreSQL / RabbitMQ | `comercios` | `comercios` |

## Estructura del proyecto

```
comercios-microservices/
├── comercios-service/        # Microservicio de afiliación (Maven)
├── notificaciones-service/   # Microservicio de notificaciones (Gradle)
├── k8s/                      # Manifiestos de Kubernetes
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
| PostgreSQL | **5434** (host) | `comercios` / `comercios`    |
| RabbitMQ management | 15672 (panel) · 5672 (AMQP) | `comercios` / `comercios` |

Bases de datos creadas: `comercios` y `notificaciones`. El host usa el puerto
5434 para no chocar con un PostgreSQL instalado localmente (conflicto típico
en Windows); dentro de Docker sigue siendo el 5432.

## Correr comercios-service en desarrollo

```bash
cd comercios-service
mvn spring-boot:run
```

Puerto: 8080. Documentación interactiva: <http://localhost:8080/swagger-ui.html>

## Correr notificaciones-service en desarrollo

```bash
cd notificaciones-service
./gradlew bootRun        # en Windows: gradlew.bat bootRun
```

Puerto: 8081.

## Verificar el flujo completo (punta a punta)

Con la infraestructura arriba y ambos servicios corriendo:

```bash
# 1. Obtener token (credenciales demo)
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin2026!"}'

# 2. Afiliar un comercio (con el token)
curl -X POST http://localhost:8080/api/v1/comercios \
  -H "Authorization: Bearer TU_TOKEN" -H "Content-Type: application/json" \
  -d '{"ruc":"20999888777","razonSocial":"Bodega Don Lucho SAC","nombreComercial":"Don Lucho","rubro":"Bodega"}'

# 3. Aprobarlo -> dispara el evento ComercioAprobado
curl -X PATCH http://localhost:8080/api/v1/comercios/{id}/estado \
  -H "Authorization: Bearer TU_TOKEN" -H "Content-Type: application/json" \
  -d '{"estado":"APROBADO"}'

# 4. Ver la notificación generada automáticamente
curl http://localhost:8081/api/v1/notificaciones
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

## Calidad con SonarCloud

Cada push a `main` dispara un workflow de GitHub Actions que compila, prueba y
analiza ambos servicios en SonarCloud (`.github/workflows/calidad.yml`).

Configuración usada:

| Servicio | Clave de proyecto |
|----------|-------------------|
| comercios-service | `comercios-microservices-comercios-service` |
| notificaciones-service | `comercios-microservices-notificaciones-service` |

Organización de SonarCloud: `branjame45`.

Pasos para activarlo por primera vez (se hace una sola vez, desde la cuenta):

1. Ingresar a <https://sonarcloud.io> con la cuenta de GitHub (`BranJame45`).
2. Importar la organización (si aún no existe) y crear los dos proyectos
   apuntando al repositorio `comercios-microservices`, con las claves de la
   tabla anterior.
3. Generar un token en *My Account → Security* y guardarlo como secret del
   repositorio con el nombre `SONAR_TOKEN`.

Los badges de quality gate al inicio de este README se activan con el primer
análisis.

## Contenedores

Cada servicio tiene su `Dockerfile` multi-stage (build con Maven/Gradle +
JDK 21, runtime con JRE 21):

```bash
docker build -t comercios-service:1.0.0 comercios-service
docker build -t notificaciones-service:1.0.0 notificaciones-service
```

## Despliegue en Kubernetes local (kind)

> **Nota de honestidad:** este despliegue corre en un clúster **Kubernetes
> local con kind** (autogestionado, equivalente conceptual a un entorno
> on-premise), no en un clúster productivo administrado.

### Pasos

```bash
# 1. Construir las imágenes (desde la raíz del proyecto)
docker build -t comercios-service:1.0.0 comercios-service
docker build -t notificaciones-service:1.0.0 notificaciones-service

# 2. Crear el clúster kind y cargar las imágenes
kind create cluster --name comercios
kind load docker-image comercios-service:1.0.0 --name comercios
kind load docker-image notificaciones-service:1.0.0 --name comercios

# 3. Desplegar todo (namespace, secret, configmaps, postgres, rabbitmq,
#    microservicios y HPA)
kubectl apply -f k8s/

# 4. (Opcional pero recomendado) metrics-server para que el HPA lea CPU real.
#    kind no lo incluye; en kind además se necesita --kubelet-insecure-tls:
kubectl apply -f https://github.com/kubernetes-sigs/metrics-server/releases/latest/download/components.yaml
kubectl -n kube-system patch deploy metrics-server \
  --patch-file k8s/patch-metrics-server.json

# 5. Verificar
kubectl get pods -n comercios
kubectl get svc -n comercios
kubectl get hpa -n comercios

# 6. Probar los servicios desde tu máquina
kubectl port-forward svc/comercios-service 8090:8080 -n comercios
kubectl port-forward svc/notificaciones-service 8091:8081 -n comercios
kubectl port-forward svc/rabbitmq 15672:15672 -n comercios   # panel RabbitMQ
```

### Evidencia del despliegue

Salida de `kubectl get pods -n comercios` en el clúster local:

```
NAME                                      READY   STATUS    RESTARTS   AGE
comercios-service-6f886dff4c-n8gxr        1/1     Running   0          4m43s
comercios-service-6f886dff4c-rwwz4        1/1     Running   0          3m43s
notificaciones-service-59f7df99fb-rgrb4   1/1     Running   0          4m43s
postgres-9cdc45799-g4wcg                  1/1     Running   0          9m40s
rabbitmq-65ff8fd688-9rt9l                 1/1     Running   0          44s
```

Salida de `kubectl get hpa -n comercios` (con metrics-server instalado):

```
NAME                REFERENCE                      TARGETS       MINPODS   MAXPODS   REPLICAS   AGE
comercios-service   Deployment/comercios-service   cpu: 2%/70%   2         6         2          14m
```

El flujo completo (afiliar → aprobar → notificación) se verificó dentro del
clúster usando `port-forward`: aprobar un comercio generó su notificación en
`notificaciones-service`.

### Qué incluye cada manifiesto (`k8s/`)

| Archivo | Contenido |
|---------|-----------|
| `00-namespace.yaml` | Namespace `comercios` |
| `01-secrets.yaml` | Secret con credenciales demo (BD, JWT, admin) |
| `02-configmaps.yaml` | Configuración no sensible (hosts/puertos internos) |
| `03-postgres.yaml` | ConfigMap init (crea BD `notificaciones`), PVC, Deployment y Service |
| `04-rabbitmq.yaml` | Deployment y Service (AMQP + panel de administración) |
| `05-comercios-service.yaml` | Deployment (con initContainer que espera a Postgres), Service y HPA (2→6 réplicas al 70% CPU) |
| `06-notificaciones-service.yaml` | Deployment y Service |

## Estado del avance

- [x] Fase 1: monorepo de microservicios + docker-compose local
- [x] Fase 2: PostgreSQL avanzado (bloqueo optimista, paginación)
- [x] Fase 3: notificaciones-service con Gradle + RabbitMQ
- [x] Fase 4: SonarCloud + Dockerfiles multi-stage
- [x] Fase 5: despliegue en Kubernetes local (kind)
- [x] Fase 6: documentación final

> Nota: el análisis de SonarCloud se ejecuta automáticamente en cada push a
> través de GitHub Actions, usando el secret `SONAR_TOKEN` configurado en el
> repositorio.
