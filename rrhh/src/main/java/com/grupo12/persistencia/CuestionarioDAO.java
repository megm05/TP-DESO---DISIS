package com.grupo12.persistencia;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Cuestionario;
import com.grupo12.dominio.Evaluacion;
import com.grupo12.dominio.Puesto;
import java.util.List;

/** Acceso a los datos de los cuestionarios, sus bloques, respuestas y resultados. */
public interface CuestionarioDAO {

    Cuestionario obtener_por_id(int id);

    void guardar(Cuestionario cuestionario);

    /** Actualiza el cuestionario y, en cascada, sus bloques, respuestas y resultados. */
    void actualizar(Cuestionario cuestionario);

    /**
     * Cuestionario en estado ACTIVO o EN_PROCESO del candidato, con sus bloques y
     * respuestas; null si no tiene ninguno.
     */
    Cuestionario obtener_activo_por_candidato(Candidato candidato);

    /** Cuestionarios de las evaluaciones indicadas, con su candidato y sus resultados. */
    List<Cuestionario> obtener_por_evaluaciones(List<Evaluacion> evaluaciones);

    /** Cantidad de candidatos convocados en las evaluaciones del puesto. */
    int contar_por_puesto(Puesto puesto);

    /** Cantidad de cuestionarios en estado COMPLETO de las evaluaciones del puesto. */
    int contar_completos_por_puesto(Puesto puesto);
}
