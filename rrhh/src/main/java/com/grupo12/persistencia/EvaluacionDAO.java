package com.grupo12.persistencia;

import com.grupo12.dominio.Evaluacion;
import com.grupo12.dominio.Puesto;
import java.util.List;

/** Acceso a los datos de las evaluaciones. */
public interface EvaluacionDAO {

    Evaluacion obtener_por_id(int id);

    /** Guarda la evaluación y, en cascada, los cuestionarios que la componen. */
    void guardar(Evaluacion evaluacion);

    /** Evaluaciones generadas para un puesto. */
    List<Evaluacion> obtener_por_puesto(Puesto puesto);
}
