@echo off
rem Usa PostgreSQL con los datos de conexion de config.properties.
cd /d "%~dp0"
java -jar gestion-evaluaciones.jar
pause
