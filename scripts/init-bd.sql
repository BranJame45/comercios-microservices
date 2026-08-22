-- Crea la base de datos del microservicio de notificaciones.
-- La base "comercios" ya se crea automáticamente vía POSTGRES_DB.
CREATE DATABASE notificaciones;
GRANT ALL PRIVILEGES ON DATABASE notificaciones TO comercios;
