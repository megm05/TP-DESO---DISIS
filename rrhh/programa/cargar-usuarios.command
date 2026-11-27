#!/bin/sh
# Carga bd/usuarios_grupo.sql en la base del modo demo. El sistema tiene que estar cerrado.
cd "$(dirname "$0")"
if [ ! -d datos-demo ]; then
  echo "Primero ejecutá ejecutar-demo.command una vez (crea la base) y cerralo."
  exit 1
fi
java -cp lib/h2-2.2.224.jar org.h2.tools.RunScript \
  -url "jdbc:h2:file:./datos-demo/rrhh;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;IFEXISTS=TRUE" \
  -user sa -script bd/usuarios_grupo.sql \
  && echo "Usuarios cargados. Ya podés abrir ejecutar-demo.command." \
  || echo "No se pudo cargar. ¿Está el sistema abierto? Cerralo y probá de nuevo."
