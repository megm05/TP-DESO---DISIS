# Gestión de Evaluaciones de Perfiles — TP 2026 Diseño de Sistemas (Grupo 12)

Sistema para la consultora **Capit@l Humano**: gestión de preguntas, evaluación de
candidatos, cuestionarios por bloques y orden de mérito.
UTN FRSF — Diseño de Sistemas de Información — Entrega final (etapas 7 y 8).


## Casos de uso implementados

| CU | Nombre |
|---|---|
| CU001 | Autenticar usuario (soporte) |
| CU020 | Gestionar Preguntas |
| CU021 | Dar de alta Pregunta |
| CU022 | Modificar Pregunta |
| CU023 | Dar de baja Pregunta |
| CU025 | Evaluar a Candidatos |
| CU026 | Completar Cuestionario |
| CU028 | Emitir Orden de mérito |

## Interfaz según los wireframes

Esta versión adapta las pantallas a los wireframes que se presentaron en la primera
parte del trabajo: ventana con el título centrado, recuadros de filtros, Cancelar a la
izquierda y Aceptar a la derecha, mensajes en ventanas emergentes con ícono, marcas de
error junto a cada campo, lápiz y paginación en la grilla de preguntas y barra de pasos.
El contenido y los flujos de los casos de uso son los mismos que en la versión entregada;
solo cambió la capa de presentación (`presentacion/` y `src/main/resources/static/`).

## Cómo ejecutarlo

Requisito: **Java 17 o superior** (`java -version`).

### Opción rápida: modo demo (no requiere PostgreSQL)

Abrir la carpeta `programa/` y ejecutar `ejecutar-demo.command` (macOS) o
`ejecutar-demo.bat` (Windows). Se abre el navegador en http://localhost:8080.

En macOS, si no abre con doble clic: `sh programa/ejecutar-demo.command`.

### Con PostgreSQL

1. Crear una base vacía: `CREATE DATABASE rrhh;`
2. Configurar la conexión en `programa/config.properties`.
3. Ejecutar `programa/ejecutar.command` (macOS) o `programa/ejecutar.bat` (Windows).

La primera vez se crean las tablas y se cargan los datos de prueba.

### Desde el código fuente (Maven)

```
mvn package
java -jar target/gestion-evaluaciones.jar --demo
```

Opciones: `--demo` (base embebida), `--reiniciar-bd` (vuelve a crear las tablas con
los datos iniciales), `--sin-navegador`.

## Usuarios de prueba

| Perfil | Ingreso |
|---|---|
| Consultor | `CONSULTOR` / `consultor123` (también `ADMIN` / `admin123`) |
| Candidato | Tipo y número de documento + la clave que se genera en "Evaluar Candidatos" |

Usuarios del grupo (opcional): `programa/bd/usuarios_grupo.sql` agrega un consultor y un
candidato por integrante. En modo demo se carga con `programa/cargar-usuarios.command`
(o `.bat`) de la misma carpeta desde la que se abre el sistema; en PostgreSQL (15 o
superior), ejecutando el archivo en pgAdmin o psql. Si se vuelve a ejecutar, actualiza los
datos de los usuarios que ya existen (por ejemplo, el apellido) y crea los que falten.

## Estructura

```
src/main/java/com/grupo12/
  presentacion/   Pantallas (servidor web embebido del JDK)
  gestores/       Controladores de los casos de uso
  dominio/        Entidades y su comportamiento
  persistencia/   Interfaces DAO + implementaciones JDBC (PostgreSQL)
  dto/            Objetos de transferencia hacia las pantallas
  util/           Configuración, seguridad y exportación a Excel
src/main/resources/bd/   schema.sql y datos_iniciales.sql
src/test/java/           Pruebas de integración
programa/                Sistema listo para ejecutar
documentacion/           Documento de la entrega y diagramas (PlantUML + PNG)
```

## Pruebas

Las pruebas de integración recorren los flujos principales y alternativos de todos los
casos de uso. **Borran y vuelven a crear las tablas** de la base indicada:

```
mvn test-compile exec:java -Dexec.mainClass=com.grupo12.PruebasIntegracion -Dexec.classpathScope=test -Dexec.args=--demo
```

Resultado: 94 de 94 verificaciones correctas, en PostgreSQL 16 y en el modo demo.

## Documentación

`documentacion/Documentacion del sistema - TP 2026 Grupo 12` (Word y PDF): arquitectura,
diagramas de clases, DER, secuencias y despliegue, decisiones de diseño, pruebas,
manual de instalación y manual de usuario.

`documentacion/Diseños de entrada y salida - TP diseño parte 1 Revisado.pdf`: wireframes,
cuadros de campos, mensajes emergentes y salidas (entrega 1).

`documentacion/diagramas/`: cada diagrama en imagen (PNG) y en PlantUML.
