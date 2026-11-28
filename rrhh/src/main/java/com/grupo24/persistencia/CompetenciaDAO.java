package com.grupo24.persistencia;

import com.grupo24.dominio.Competencia;
import java.util.List;

/** Acceso a los datos de las competencias (con sus factores y preguntas). */
public interface CompetenciaDAO {

    Competencia obtener_por_id(int id);

    List<Competencia> buscar_por_nombre(String nombre);
}
