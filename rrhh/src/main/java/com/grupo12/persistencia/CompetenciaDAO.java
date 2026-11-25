package com.grupo12.persistencia;

import com.grupo12.dominio.Competencia;
import java.util.List;

/** Acceso a los datos de las competencias (con sus factores y preguntas). */
public interface CompetenciaDAO {

    Competencia obtener_por_id(int id);

    /** Competencias cuyo nombre contiene el texto; todas si el texto es vacío o nulo. */
    List<Competencia> buscar_por_nombre(String nombre);
}
