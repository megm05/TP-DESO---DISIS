package com.grupo12.persistencia;

import com.grupo12.dominio.Competencia;
import com.grupo12.dominio.Factor;
import com.grupo12.dominio.Pregunta;
import java.util.List;

/** Acceso a los datos de las preguntas y de las ponderaciones de sus opciones. */
public interface PreguntaDAO {

    Pregunta obtener_por_id(int id);

    void guardar(Pregunta pregunta);

    void actualizar(Pregunta pregunta);

    /**
     * Búsqueda de preguntas vigentes (CU020). El criterio de búsqueda es "contiene";
     * los criterios vacíos o nulos no se aplican.
     */
    List<Pregunta> buscar_por_filtros(String nombre_competencia, String nombre_factor, String nombre_pregunta);

    /** Preguntas vigentes de un factor, con sus opciones de respuesta (CU026 paso 3). */
    List<Pregunta> obtener_activas_por_factor(Factor factor);

    /**
     * Indica si ya existe una pregunta vigente definida para esa competencia y factor
     * con el mismo nombre o con el mismo texto (CU021 flujo 2.C). El criterio que se
     * recibe en null no se evalúa.
     *
     * @param id_excluida pregunta que no debe considerarse (la que se está modificando); 0 en el alta
     */
    boolean existe_pregunta(String nombre, String pregunta, Competencia competencia, Factor factor, int id_excluida);
}
