package com.grupo12.persistencia;

import com.grupo12.dominio.Evaluacion;
import com.grupo12.dominio.Puesto;
import java.util.List;

/** Acceso a los datos de las evaluaciones. */
public interface EvaluacionDAO {

    Evaluacion obtener_por_id(int id);

    void guardar(Evaluacion evaluacion);

    List<Evaluacion> obtener_por_puesto(Puesto puesto);
}
