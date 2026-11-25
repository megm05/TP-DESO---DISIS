package com.grupo12.persistencia;

import com.grupo12.dominio.Factor;
import java.util.List;

/** Acceso a los datos de los factores (con sus preguntas). */
public interface FactorDAO {

    Factor obtener_por_id(int id);

    /** Factores que componen una competencia. */
    List<Factor> obtener_por_competencia(int id_competencia);
}
