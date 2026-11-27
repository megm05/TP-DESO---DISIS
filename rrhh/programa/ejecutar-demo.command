#!/bin/sh
# Modo demo: usa una base de datos embebida, no requiere PostgreSQL.
cd "$(dirname "$0")"
java -jar gestion-evaluaciones.jar --demo
