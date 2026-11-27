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

    void actualizar(Cuestionario cuestionario);

    Cuestionario obtener_activo_por_candidato(Candidato candidato);

    List<Cuestionario> obtener_por_evaluaciones(List<Evaluacion> evaluaciones);

    int contar_por_puesto(Puesto puesto);

    int contar_completos_por_puesto(Puesto puesto);
}
