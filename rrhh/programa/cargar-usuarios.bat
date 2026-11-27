@echo off
rem Carga bd\usuarios_grupo.sql en la base del modo demo. El sistema tiene que estar cerrado.
cd /d "%~dp0"
if not exist datos-demo (
  echo Primero ejecute ejecutar-demo.bat una vez y cierrelo.
  pause
  exit /b 1
)
java -cp lib\h2-2.2.224.jar org.h2.tools.RunScript -url "jdbc:h2:file:./datos-demo/rrhh;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;IFEXISTS=TRUE" -user sa -script bd\usuarios_grupo.sql
if errorlevel 1 (echo No se pudo cargar. Cierre el sistema y pruebe de nuevo.) else (echo Usuarios cargados.)
pause
