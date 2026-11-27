#!/bin/sh
# Usa PostgreSQL con los datos de conexión de config.properties.
cd "$(dirname "$0")"
java -jar gestion-evaluaciones.jar
