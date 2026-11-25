package com.grupo12.persistencia;

import com.grupo12.dominio.Respuesta;

/** Acceso a los datos de las respuestas de un cuestionario. */
public interface RespuestaDAO {

    /** Guarda la opción elegida por el candidato para una pregunta. */
    void actualizar(Respuesta respuesta);
}
