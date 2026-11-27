-- ==============================================================
--  Usuarios del grupo para probar el sistema (opcional)
--  No forma parte de los datos iniciales de la entrega.
-- ==============================================================
--  Cómo cargarlo:
--    Modo demo:   ejecutar cargar-usuarios.command (o cargar-usuarios.bat en Windows)
--                 de la MISMA carpeta "programa" desde la que se abre el sistema.
--    PostgreSQL:  ejecutar este archivo en pgAdmin (Query Tool) o en psql
--                 (requiere PostgreSQL 15 o superior).
--  Se puede ejecutar todas las veces que se quiera: si el usuario ya existe se
--  actualizan sus datos (por ejemplo, el apellido) y si no existe se crea.
--
--  Para cambiar un dato, editarlo en la línea "SELECT ..." de ese usuario y volver
--  a ejecutar cargar-usuarios. Los datos van en MAYÚSCULAS.
--
--  Para agregar un consultor, copiar un bloque MERGE completo (hasta el punto y coma)
--  y cambiar en su línea SELECT:
--    - el número (id): uno que no esté usado (por ejemplo 15, 16...).
--    - el usuario: en MAYÚSCULAS, hasta 15 caracteres.
--    - la clave: el hash SHA-256 de la contraseña. Se obtiene en la Terminal con
--          printf %s 'la_contraseña' | shasum -a 256
--      (en Windows, PowerShell:  (Get-FileHash -Algorithm SHA256 -InputStream
--       ([IO.MemoryStream]::new([Text.Encoding]::UTF8.GetBytes('la_contraseña')))).Hash.ToLower() )
--    - el nombre y el apellido.
--
--  Para agregar un candidato, copiar un bloque MERGE de candidato y cambiar el id y
--  el número de candidato (que no estén usados), el documento y los datos personales.
--  El tipo de documento puede ser DNI, LE, LC o PP.
-- ==============================================================

-- ------------------------------------------------------------
-- Consultores (usuario / contraseña)
-- ------------------------------------------------------------

-- JC / jc123
MERGE INTO Consultor c USING (SELECT
    11 AS id, 'JC' AS usuario, 'edbcb44e9370353f26c4e25a1cb7b820dc84221cad22c4b577dfb434058cf9dc' AS clave, 'JUAN CRUZ' AS nombre, 'TORTOLO' AS apellido
  ) v ON c.id_consultor = v.id
  WHEN MATCHED THEN UPDATE SET nombre_usuario = v.usuario, contrasenia = v.clave, nombre = v.nombre, apellido = v.apellido
  WHEN NOT MATCHED THEN INSERT (id_consultor, nombre_usuario, contrasenia, nombre, apellido)
    VALUES (v.id, v.usuario, v.clave, v.nombre, v.apellido);

-- MATIAS / matias123
MERGE INTO Consultor c USING (SELECT
    12 AS id, 'MATIAS' AS usuario, '1e5bdf9b1fb0369c90d3fcadcb9813d0e95752e97837c8266dd58a202d6c4fcf' AS clave, 'MATIAS' AS nombre, 'GOMEZ MOLINAS' AS apellido
  ) v ON c.id_consultor = v.id
  WHEN MATCHED THEN UPDATE SET nombre_usuario = v.usuario, contrasenia = v.clave, nombre = v.nombre, apellido = v.apellido
  WHEN NOT MATCHED THEN INSERT (id_consultor, nombre_usuario, contrasenia, nombre, apellido)
    VALUES (v.id, v.usuario, v.clave, v.nombre, v.apellido);

-- LAUTARO / lautaro123
MERGE INTO Consultor c USING (SELECT
    13 AS id, 'LAUTARO' AS usuario, '2e7788ba0ffc156ddf8ba9ad297a4bb7923a3ee50c0bd9ae6d19abfb4df52372' AS clave, 'LAUTARO' AS nombre, 'MARTINEZ' AS apellido
  ) v ON c.id_consultor = v.id
  WHEN MATCHED THEN UPDATE SET nombre_usuario = v.usuario, contrasenia = v.clave, nombre = v.nombre, apellido = v.apellido
  WHEN NOT MATCHED THEN INSERT (id_consultor, nombre_usuario, contrasenia, nombre, apellido)
    VALUES (v.id, v.usuario, v.clave, v.nombre, v.apellido);

-- BAUTI / bauti123
MERGE INTO Consultor c USING (SELECT
    14 AS id, 'BAUTI' AS usuario, '27a9d09caa13dc99f324eabe6243485766aa565033edf6beb5ba2d584ab056a9' AS clave, 'BAUTISTA' AS nombre, 'PINTO' AS apellido
  ) v ON c.id_consultor = v.id
  WHEN MATCHED THEN UPDATE SET nombre_usuario = v.usuario, contrasenia = v.clave, nombre = v.nombre, apellido = v.apellido
  WHEN NOT MATCHED THEN INSERT (id_consultor, nombre_usuario, contrasenia, nombre, apellido)
    VALUES (v.id, v.usuario, v.clave, v.nombre, v.apellido);

-- ------------------------------------------------------------
-- Candidatos (documentos ficticios). Para entrar a "Realizar cuestionario"
-- hace falta la clave que se genera en Evaluar Candidatos.
-- ------------------------------------------------------------

MERGE INTO Candidato c USING (SELECT
    101 AS id, 2001 AS numero, 'DNI' AS tipo_doc, '42000001' AS nro_doc, 'JUAN CRUZ' AS nombre, 'TORTOLO' AS apellido,
    'jc@example.com' AS email, 'H' AS genero, DATE '2002-04-10' AS fecha_nac, 'UNIVERSITARIO INCOMPLETO' AS escolaridad
  ) v ON c.id_candidato = v.id
  WHEN MATCHED THEN UPDATE SET num_candidato = v.numero, tipo_doc = v.tipo_doc, nro_doc = v.nro_doc, nombre = v.nombre,
    apellido = v.apellido, email = v.email, genero = v.genero, fecha_nac = v.fecha_nac, escolaridad = v.escolaridad
  WHEN NOT MATCHED THEN INSERT (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad)
    VALUES (v.id, v.numero, v.tipo_doc, v.nro_doc, v.nombre, v.apellido, v.email, v.genero, v.fecha_nac, v.escolaridad);

MERGE INTO Candidato c USING (SELECT
    102 AS id, 2002 AS numero, 'DNI' AS tipo_doc, '42000002' AS nro_doc, 'MATIAS' AS nombre, 'GOMEZ MOLINAS' AS apellido,
    'matias@example.com' AS email, 'H' AS genero, DATE '2002-08-22' AS fecha_nac, 'UNIVERSITARIO INCOMPLETO' AS escolaridad
  ) v ON c.id_candidato = v.id
  WHEN MATCHED THEN UPDATE SET num_candidato = v.numero, tipo_doc = v.tipo_doc, nro_doc = v.nro_doc, nombre = v.nombre,
    apellido = v.apellido, email = v.email, genero = v.genero, fecha_nac = v.fecha_nac, escolaridad = v.escolaridad
  WHEN NOT MATCHED THEN INSERT (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad)
    VALUES (v.id, v.numero, v.tipo_doc, v.nro_doc, v.nombre, v.apellido, v.email, v.genero, v.fecha_nac, v.escolaridad);

MERGE INTO Candidato c USING (SELECT
    103 AS id, 2003 AS numero, 'DNI' AS tipo_doc, '42000003' AS nro_doc, 'LAUTARO' AS nombre, 'MARTINEZ' AS apellido,
    'lautaro@example.com' AS email, 'H' AS genero, DATE '2003-01-15' AS fecha_nac, 'UNIVERSITARIO INCOMPLETO' AS escolaridad
  ) v ON c.id_candidato = v.id
  WHEN MATCHED THEN UPDATE SET num_candidato = v.numero, tipo_doc = v.tipo_doc, nro_doc = v.nro_doc, nombre = v.nombre,
    apellido = v.apellido, email = v.email, genero = v.genero, fecha_nac = v.fecha_nac, escolaridad = v.escolaridad
  WHEN NOT MATCHED THEN INSERT (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad)
    VALUES (v.id, v.numero, v.tipo_doc, v.nro_doc, v.nombre, v.apellido, v.email, v.genero, v.fecha_nac, v.escolaridad);

MERGE INTO Candidato c USING (SELECT
    104 AS id, 2004 AS numero, 'DNI' AS tipo_doc, '42000004' AS nro_doc, 'BAUTISTA' AS nombre, 'PINTO' AS apellido,
    'bauti@example.com' AS email, 'H' AS genero, DATE '2003-05-30' AS fecha_nac, 'UNIVERSITARIO INCOMPLETO' AS escolaridad
  ) v ON c.id_candidato = v.id
  WHEN MATCHED THEN UPDATE SET num_candidato = v.numero, tipo_doc = v.tipo_doc, nro_doc = v.nro_doc, nombre = v.nombre,
    apellido = v.apellido, email = v.email, genero = v.genero, fecha_nac = v.fecha_nac, escolaridad = v.escolaridad
  WHEN NOT MATCHED THEN INSERT (id_candidato, num_candidato, tipo_doc, nro_doc, nombre, apellido, email, genero, fecha_nac, escolaridad)
    VALUES (v.id, v.numero, v.tipo_doc, v.nro_doc, v.nombre, v.apellido, v.email, v.genero, v.fecha_nac, v.escolaridad);
