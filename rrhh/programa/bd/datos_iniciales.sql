-- =====================================================================
--  Datos iniciales de prueba
--  (parámetros, consultores, empresas, competencias, factores, opciones
--   de respuesta, preguntas, puestos y candidatos)
-- =====================================================================

-- Repositorio de configuraciones
INSERT INTO Parametro (clave, valor, descripcion) VALUES ('INSTRUCCIONES', 'BIENVENIDO/A AL PROCESO DE EVALUACION DE CAPIT@L HUMANO. A CONTINUACION SE LE PRESENTARA UN CUESTIONARIO DE OPCION MULTIPLE CON UNA UNICA RESPUESTA POR PREGUNTA. LAS PREGUNTAS SE MUESTRAN EN BLOQUES: DEBE RESPONDER TODAS LAS PREGUNTAS DE UN BLOQUE PARA AVANZAR AL SIGUIENTE. UNA VEZ COMPLETADO UN BLOQUE NO PODRA VOLVER A VERLO NI MODIFICARLO. PUEDE INTERRUMPIR EL CUESTIONARIO Y CONTINUARLO MAS TARDE CON LA MISMA CLAVE, DENTRO DEL TIEMPO MAXIMO Y DE LA CANTIDAD DE ACCESOS PERMITIDOS. RESPONDA CON SINCERIDAD: NO HAY LIMITE DE TIEMPO POR PREGUNTA.', 'Instrucciones que se muestran al candidato antes de iniciar el cuestionario (CU026)');
INSERT INTO Parametro (clave, valor, descripcion) VALUES ('PREGUNTAS_POR_BLOQUE', '4', 'Cantidad de preguntas por cada paso (bloque) del cuestionario');
INSERT INTO Parametro (clave, valor, descripcion) VALUES ('TIEMPO_MAX_COMPLETAR', '72', 'Horas maximas para completar el cuestionario desde el primer ingreso');
INSERT INTO Parametro (clave, valor, descripcion) VALUES ('TIEMPO_MAX_ACTIVO', '360', 'Horas maximas que un cuestionario puede permanecer en estado ACTIVO sin ser iniciado');
INSERT INTO Parametro (clave, valor, descripcion) VALUES ('MAX_ACCESOS', '5', 'Cantidad maxima de accesos permitidos al cuestionario');

-- Consultores (la contraseña se almacena como hash SHA-256)
--   CONSULTOR / consultor123      ADMIN / admin123
INSERT INTO Consultor (id_consultor, nombre_usuario, contrasenia, nombre, apellido) VALUES (1, 'CONSULTOR', '7e31b85bb0182bf78c7ee2f6ed34e746292d8cff61320390cf685d59f878381a', 'LAURA', 'FERNANDEZ');
INSERT INTO Consultor (id_consultor, nombre_usuario, contrasenia, nombre, apellido) VALUES (2, 'ADMIN', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'MARTIN', 'GOMEZ');

-- Empresas
INSERT INTO Empresa (id_empresa, nombre) VALUES (1, 'TECNOSUR S.A.');
INSERT INTO Empresa (id_empresa, nombre) VALUES (2, 'GLOBALSOFT ARGENTINA');
INSERT INTO Empresa (id_empresa, nombre) VALUES (3, 'BANCO DEL LITORAL');

-- Competencias
INSERT INTO Competencia (id_competencia, nombre, tipo) VALUES (1, 'GESTION DE PROYECTOS', 'TECNICA');
INSERT INTO Competencia (id_competencia, nombre, tipo) VALUES (2, 'PROGRAMACION ORIENTADA A OBJETOS', 'TECNICA');
INSERT INTO Competencia (id_competencia, nombre, tipo) VALUES (3, 'BASES DE DATOS', 'TECNICA');
INSERT INTO Competencia (id_competencia, nombre, tipo) VALUES (4, 'LEALTAD', 'ACTITUDINAL');
INSERT INTO Competencia (id_competencia, nombre, tipo) VALUES (5, 'TRABAJO EN EQUIPO', 'ACTITUDINAL');
INSERT INTO Competencia (id_competencia, nombre, tipo) VALUES (6, 'LIDERAZGO', 'ACTITUDINAL');

-- Factores
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (1, 1, 'GESTION DEL ALCANCE', 'DEFINICION Y CONTROL DEL ALCANCE DEL PROYECTO');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (2, 1, 'GESTION DEL TIEMPO', 'PLANIFICACION Y CONTROL DEL CRONOGRAMA');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (3, 1, 'GESTION DE RIESGOS', 'IDENTIFICACION Y TRATAMIENTO DE RIESGOS');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (4, 2, 'DISEÑO DE CLASES', 'CONCEPTOS DE CLASES, OBJETOS Y SUS RELACIONES');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (5, 2, 'PATRONES DE DISEÑO', 'CONOCIMIENTO Y APLICACION DE PATRONES');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (6, 3, 'MODELADO DE DATOS', 'MODELO ENTIDAD-RELACION Y NORMALIZACION');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (7, 3, 'LENGUAJE SQL', 'CONSULTAS Y MANIPULACION DE DATOS');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (8, 4, 'CREDIBILIDAD', 'CONFIANZA QUE GENERA EN SU ENTORNO LABORAL');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (9, 4, 'COMPROMISO', 'IDENTIFICACION CON LOS OBJETIVOS DE LA ORGANIZACION');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (10, 5, 'COMUNICACION', 'INTERCAMBIO DE INFORMACION CON EL EQUIPO');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (11, 5, 'COLABORACION', 'DISPOSICION PARA TRABAJAR CON OTROS');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (12, 6, 'CONDUCCION DE EQUIPOS', 'CAPACIDAD PARA GUIAR A UN GRUPO DE TRABAJO');
INSERT INTO Factor (id_factor, id_competencia, nombre, descripcion) VALUES (13, 6, 'TOMA DE DECISIONES', 'CAPACIDAD PARA DECIDIR BAJO PRESION');

-- Opciones de respuesta y su lista de opciones (orden, descripcion)
INSERT INTO Opcion_respuesta (id_opcion, nombre, descripcion) VALUES (1, 'VERDADERO - FALSO', 'RESPUESTA DE VERDADERO O FALSO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (1, 1, 1, 'VERDADERO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (2, 1, 2, 'FALSO');
INSERT INTO Opcion_respuesta (id_opcion, nombre, descripcion) VALUES (2, 'SI - NO', 'RESPUESTA POR SI O POR NO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (3, 2, 1, 'SI');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (4, 2, 2, 'NO');
INSERT INTO Opcion_respuesta (id_opcion, nombre, descripcion) VALUES (3, 'ESCALA DE FRECUENCIA', 'FRECUENCIA CON LA QUE SE PRESENTA UNA CONDUCTA');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (5, 3, 1, 'NUNCA');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (6, 3, 2, 'A VECES');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (7, 3, 3, 'FRECUENTEMENTE');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (8, 3, 4, 'SIEMPRE');
INSERT INTO Opcion_respuesta (id_opcion, nombre, descripcion) VALUES (4, 'ESCALA DE ACUERDO', 'GRADO DE ACUERDO CON UNA AFIRMACION');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (9, 4, 1, 'TOTALMENTE EN DESACUERDO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (10, 4, 2, 'EN DESACUERDO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (11, 4, 3, 'NI DE ACUERDO NI EN DESACUERDO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (12, 4, 4, 'DE ACUERDO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (13, 4, 5, 'TOTALMENTE DE ACUERDO');
INSERT INTO Opcion_respuesta (id_opcion, nombre, descripcion) VALUES (5, 'ESCALA DE CALIDAD', 'VALORACION DE CALIDAD');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (14, 5, 1, 'MALO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (15, 5, 2, 'REGULAR');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (16, 5, 3, 'BUENO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (17, 5, 4, 'MUY BUENO');
INSERT INTO Detalle_opcion (id_detalle, id_opcion, orden, descripcion) VALUES (18, 5, 5, 'EXCELENTE');

-- Preguntas y ponderaciones de sus opciones (cada pregunta suma 10 puntos)
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (1, 1, 1, 'WBS EN EL ALCANCE', '¿LA DESCOMPOSICION DE ESTRUCTURA DE TRABAJO (WBS) FORMA PARTE DE LA DEFINICION DEL ALCANCE?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (1, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (1, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (2, 1, 1, 'LINEA BASE DE REQUERIMIENTOS', 'CUANDO SE DEFINE UN ALCANCE, ¿DEBE DEFINIRSE UNA LINEA BASE DE LOS REQUERIMIENTOS?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (2, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (2, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (3, 1, 1, 'CONTROL DE CAMBIOS DEL ALCANCE', '¿TODO CAMBIO EN EL ALCANCE DEBE PASAR POR UN PROCESO FORMAL DE CONTROL DE CAMBIOS?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (3, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (3, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (4, 2, 1, 'DEPENDENCIA FIN A COMIENZO', 'EN EL CRONOGRAMA DE UN PROYECTO, ¿UNA ACTIVIDAD DEL TIPO FIN A COMIENZO SOLO PUEDE INICIAR CUANDO FINALIZA SU PREDECESORA?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (4, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (4, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (5, 2, 1, 'VALOR GANADO', '¿LA METODOLOGIA DE VALOR GANADO PERMITE PREDECIR CUANTO DINERO SE GASTARA EN UN PROYECTO SEGUN LOS AVANCES QUE EL MISMO TENGA A LA FECHA?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (5, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (5, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (6, 2, 1, 'CAMINO CRITICO', '¿EL CAMINO CRITICO ES LA SECUENCIA DE ACTIVIDADES CON MAYOR HOLGURA DEL PROYECTO?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (6, 1, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (6, 2, 10);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (7, 3, 1, 'IDENTIFICACION DE RIESGOS', '¿LA IDENTIFICACION DE RIESGOS SE REALIZA UNA UNICA VEZ AL INICIO DEL PROYECTO?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (7, 1, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (7, 2, 10);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (8, 3, 3, 'REVISION DEL PLAN DE RIESGOS', '¿CON QUE FRECUENCIA DEBE REVISARSE EL PLAN DE RESPUESTA A LOS RIESGOS DURANTE EL PROYECTO?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (8, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (8, 6, 1);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (8, 7, 4);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (8, 8, 5);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (9, 3, 1, 'RIESGOS POSITIVOS', '¿UN RIESGO PUEDE REPRESENTAR UNA OPORTUNIDAD PARA EL PROYECTO?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (9, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (9, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (10, 4, 1, 'ENCAPSULAMIENTO', '¿EL ENCAPSULAMIENTO CONSISTE EN OCULTAR EL ESTADO INTERNO DE UN OBJETO Y EXPONER SOLO SU COMPORTAMIENTO?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (10, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (10, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (11, 4, 1, 'HERENCIA MULTIPLE', '¿EN JAVA UNA CLASE PUEDE HEREDAR DE MAS DE UNA CLASE A LA VEZ?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (11, 1, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (11, 2, 10);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (12, 4, 1, 'COMPOSICION', '¿EN UNA RELACION DE COMPOSICION LAS PARTES PUEDEN EXISTIR SIN EL TODO?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (12, 1, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (12, 2, 10);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (13, 5, 1, 'PATRON SINGLETON', '¿EL PATRON SINGLETON GARANTIZA UNA UNICA INSTANCIA DE UNA CLASE?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (13, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (13, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (14, 5, 1, 'PATRON DAO', '¿EL PATRON DAO SEPARA LA LOGICA DE NEGOCIO DEL ACCESO A LOS DATOS?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (14, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (14, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (15, 5, 1, 'PATRON DTO', '¿UN DTO DEBE CONTENER LA LOGICA DE NEGOCIO DE LA APLICACION?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (15, 1, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (15, 2, 10);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (16, 6, 1, 'CLAVE PRIMARIA', '¿UNA CLAVE PRIMARIA PUEDE CONTENER VALORES NULOS?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (16, 1, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (16, 2, 10);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (17, 6, 1, 'TERCERA FORMA NORMAL', '¿LA TERCERA FORMA NORMAL ELIMINA LAS DEPENDENCIAS TRANSITIVAS?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (17, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (17, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (18, 6, 1, 'RELACION MUCHOS A MUCHOS', '¿UNA RELACION MUCHOS A MUCHOS SE RESUELVE CON UNA TABLA INTERMEDIA?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (18, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (18, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (19, 7, 1, 'CLAUSULA WHERE', '¿LA CLAUSULA WHERE FILTRA LAS FILAS ANTES DEL AGRUPAMIENTO?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (19, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (19, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (20, 7, 1, 'INNER JOIN', '¿UN INNER JOIN DEVUELVE TAMBIEN LAS FILAS SIN COINCIDENCIA?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (20, 1, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (20, 2, 10);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (21, 7, 1, 'CLAUSULA HAVING', '¿LA CLAUSULA HAVING FILTRA LOS GRUPOS RESULTANTES DE UN GROUP BY?', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (21, 1, 10);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (21, 2, 0);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (22, 8, 4, 'CONFIANZA PROFESIONAL', 'SOY DIGNO DE CONFIANZA PORQUE LO DEMUESTRO A TRAVES DE MIS HECHOS PROFESIONALES.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (22, 9, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (22, 10, 1);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (22, 11, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (22, 12, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (22, 13, 4);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (23, 8, 4, 'CUMPLIMIENTO INTEGRO', 'CUMPLO DE MANERA INTEGRA CON LO QUE SE ME PIDE EN EL TRABAJO.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (23, 9, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (23, 10, 1);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (23, 11, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (23, 12, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (23, 13, 4);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (24, 8, 4, 'RESGUARDO DE INTERESES', 'GUARDO LOS INTERESES DE LA EMPRESA SOBRE CUALQUIER SITUACION QUE PUDIERA AFECTARLA.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (24, 9, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (24, 10, 1);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (24, 11, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (24, 12, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (24, 13, 4);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (25, 9, 3, 'PROYECTOS ASIGNADOS', 'ME COMPROMETO CON LOS PROYECTOS QUE ME ASIGNAN EN EL TRABAJO.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (25, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (25, 6, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (25, 7, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (25, 8, 5);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (26, 9, 3, 'PROYECTOS ESTRATEGICOS', 'CUANDO ME ASIGNAN PROYECTOS ESTRATEGICOS PARA LA EMPRESA, ME HAGO CARGO DE ELLOS TOTALMENTE.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (26, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (26, 6, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (26, 7, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (26, 8, 5);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (27, 9, 3, 'OBJETIVOS COLECTIVOS', 'PRIORIZO EL LOGRO DE LOS OBJETIVOS COLECTIVOS POR SOBRE LOS INDIVIDUALES.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (27, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (27, 6, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (27, 7, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (27, 8, 5);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (28, 10, 3, 'ESCUCHA ACTIVA', 'ESCUCHO CON ATENCION LAS OPINIONES DE MIS COMPAÑEROS ANTES DE EXPRESAR LA MIA.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (28, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (28, 6, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (28, 7, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (28, 8, 5);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (29, 10, 3, 'INFORMACION COMPARTIDA', 'COMPARTO CON EL EQUIPO LA INFORMACION RELEVANTE PARA EL TRABAJO.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (29, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (29, 6, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (29, 7, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (29, 8, 5);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (30, 10, 3, 'RETROALIMENTACION', 'DOY Y RECIBO RETROALIMENTACION DE MANERA CONSTRUCTIVA.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (30, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (30, 6, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (30, 7, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (30, 8, 5);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (31, 11, 4, 'AYUDA A COMPAÑEROS', 'SOY PARTIDARIO DE AYUDAR A MIS COMPAÑEROS DE TRABAJO CON TAL DE BENEFICIAR LOS INTERESES LABORALES.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (31, 9, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (31, 10, 1);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (31, 11, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (31, 12, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (31, 13, 4);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (32, 11, 4, 'TRABAJO CONJUNTO', 'PREFIERO RESOLVER LOS PROBLEMAS COMPLEJOS JUNTO A MI EQUIPO.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (32, 9, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (32, 10, 1);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (32, 11, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (32, 12, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (32, 13, 4);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (33, 11, 4, 'RECONOCIMIENTO', 'RECONOZCO LOS APORTES DE LOS DEMAS EN LOS LOGROS DEL EQUIPO.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (33, 9, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (33, 10, 1);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (33, 11, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (33, 12, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (33, 13, 4);
INSERT INTO Pregunta (id_pregunta, id_factor, id_opcion, nombre, pregunta, descripcion, activo) VALUES (34, 12, 3, 'DELEGACION', 'DELEGO TAREAS CONFIANDO EN LA CAPACIDAD DE MI EQUIPO.', NULL, TRUE);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (34, 5, 0);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (34, 6, 2);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (34, 7, 3);
INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (34, 8, 5);

-- Puestos y caracteristicas del puesto (competencia + ponderacion minima requerida)
INSERT INTO Puesto (id_puesto, id_empresa, nombre, descripcion) VALUES (1, 1, 'ANALISTA PROGRAMADOR JAVA', 'DESARROLLO Y MANTENIMIENTO DE APLICACIONES JAVA');
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (1, 2, 6.0);
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (1, 3, 5.0);
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (1, 5, 2.0);
INSERT INTO Puesto (id_puesto, id_empresa, nombre, descripcion) VALUES (2, 2, 'LIDER DE PROYECTO', 'CONDUCCION DE PROYECTOS DE DESARROLLO DE SOFTWARE');
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (2, 1, 6.0);
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (2, 4, 2.0);
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (2, 5, 2.0);
INSERT INTO Puesto (id_puesto, id_empresa, nombre, descripcion) VALUES (3, 3, 'ADMINISTRADOR DE BASES DE DATOS', 'ADMINISTRACION Y OPTIMIZACION DE BASES DE DATOS');
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (3, 3, 7.0);
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (3, 4, 1.5);
INSERT INTO Puesto (id_puesto, id_empresa, nombre, descripcion) VALUES (4, 3, 'GERENTE DE SISTEMAS', 'DIRECCION DEL AREA DE SISTEMAS');
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (4, 1, 7.0);
INSERT INTO Puesto_Competencia (id_puesto, id_competencia, ponderacion_minima) VALUES (4, 6, 5.0);

-- Candidatos (en producción se importan desde el sistema de Gestión de Candidatos y CV - CU006)
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (1, 1001, 'DNI', '30111222', 'MARIA LAURA', 'ACOSTA', 'maria.acosta@example.com', 'M', DATE '1984-03-12', 'UNIVERSITARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (2, 1002, 'DNI', '31222333', 'JUAN PABLO', 'BENITEZ', 'juan.benitez@example.com', 'H', DATE '1985-07-25', 'UNIVERSITARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (3, 1003, 'DNI', '32333444', 'CAROLINA', 'CABRERA', 'carolina.cabrera@example.com', 'M', DATE '1987-11-02', 'TERCIARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (4, 1004, 'DNI', '33444555', 'DIEGO', 'DOMINGUEZ', 'diego.dominguez@example.com', 'H', DATE '1988-01-19', 'UNIVERSITARIO INCOMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (5, 1005, 'DNI', '34555666', 'FLORENCIA', 'ESCOBAR', 'florencia.escobar@example.com', 'M', DATE '1990-05-30', 'UNIVERSITARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (6, 1006, 'DNI', '35666777', 'GONZALO', 'FERREYRA', 'gonzalo.ferreyra@example.com', 'H', DATE '1991-09-14', 'SECUNDARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (7, 1007, 'DNI', '36777888', 'LUCIA', 'GIMENEZ', 'lucia.gimenez@example.com', 'M', DATE '1992-12-08', 'UNIVERSITARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (8, 1008, 'DNI', '37888999', 'MATIAS', 'HERRERA', 'matias.herrera@example.com', 'H', DATE '1993-04-21', 'TERCIARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (9, 1009, 'DNI', '38999000', 'NATALIA', 'IBAÑEZ', 'natalia.ibanez@example.com', 'M', DATE '1994-08-03', 'UNIVERSITARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (10, 1010, 'LE', '5123456', 'OSCAR', 'JUAREZ', 'oscar.juarez@example.com', 'H', DATE '1965-02-17', 'UNIVERSITARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (11, 1011, 'LC', '4987654', 'PATRICIA', 'LEDESMA', 'patricia.ledesma@example.com', 'M', DATE '1968-06-09', 'TERCIARIO COMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (12, 1012, 'PP', 'AAB123456', 'RICARDO', 'MOLINA', 'ricardo.molina@example.com', 'H', DATE '1989-10-27', 'POSGRADO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (13, 1013, 'DNI', '40111222', 'SOFIA', 'NAVARRO', 'sofia.navarro@example.com', 'M', DATE '1997-03-05', 'UNIVERSITARIO INCOMPLETO');
INSERT INTO Candidato (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad) VALUES (14, 1014, 'DNI', '41222333', 'TOMAS', 'ORTIZ', 'tomas.ortiz@example.com', 'H', DATE '1998-07-16', 'UNIVERSITARIO COMPLETO');

-- Reinicio de las secuencias de identidad luego de insertar con id explícito
ALTER TABLE Consultor ALTER COLUMN id_consultor RESTART WITH 3;
ALTER TABLE Empresa ALTER COLUMN id_empresa RESTART WITH 4;
ALTER TABLE Competencia ALTER COLUMN id_competencia RESTART WITH 7;
ALTER TABLE Factor ALTER COLUMN id_factor RESTART WITH 14;
ALTER TABLE Opcion_respuesta ALTER COLUMN id_opcion RESTART WITH 6;
ALTER TABLE Detalle_opcion ALTER COLUMN id_detalle RESTART WITH 19;
ALTER TABLE Pregunta ALTER COLUMN id_pregunta RESTART WITH 35;
ALTER TABLE Puesto ALTER COLUMN id_puesto RESTART WITH 5;
ALTER TABLE Candidato ALTER COLUMN id_candidato RESTART WITH 15;
