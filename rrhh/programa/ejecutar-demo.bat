@echo off
rem Modo demo: usa una base de datos embebida, no requiere PostgreSQL.
cd /d "%~dp0"
java -jar gestion-evaluaciones.jar --demo
pause
