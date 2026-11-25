# Gestión de Evaluaciones de Perfiles — TP 2026 Diseño de Sistemas

Sistema para la consultora **Capit@l Humano** (UTN FRSF — Diseño de Sistemas de Información).
Java 17 + PostgreSQL (JDBC), proyecto Maven.

## Estado

En desarrollo. Están el modelo de dominio completo y la capa de persistencia; faltan los
gestores de los casos de uso y las pantallas.

```
src/main/java/com/grupo12/
  dominio/        Entidades y su comportamiento
  dto/            Objetos de transferencia hacia las pantallas
  persistencia/   Interfaces DAO + implementaciones JDBC (PostgreSQL)
  util/           Configuración, seguridad y exportación a Excel
src/main/resources/
  bd/             schema.sql (tablas) y datos_iniciales.sql (datos de prueba)
  static/         Estilos y script de las pantallas
```

## Base de datos

1. Crear una base vacía: `CREATE DATABASE rrhh;`
2. Ejecutar `src/main/resources/bd/schema.sql` y luego `datos_iniciales.sql`.
