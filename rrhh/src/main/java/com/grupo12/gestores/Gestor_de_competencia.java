package com.grupo12.gestores;

import com.grupo12.dominio.Competencia;
import com.grupo12.persistencia.CompetenciaDAO;
import com.grupo12.persistencia.FabricaDAO;
import java.util.List;

/**
 * Gestor de competencias. En esta entrega solo se implementa la búsqueda, que
 * usa el alta de preguntas (CU021); el ABM de competencias (CU012 a CU015) queda
 * fuera del alcance de la implementación.
 */
public class Gestor_de_competencia {

    private final CompetenciaDAO competenciaDAO;

    public Gestor_de_competencia() {
        this(FabricaDAO.getCompetenciaDAO());
    }

    public Gestor_de_competencia(CompetenciaDAO competenciaDAO) {
        this.competenciaDAO = competenciaDAO;
    }

    /** Competencias cuyo nombre contiene el texto; todas si no se indica ninguno. */
    public List<Competencia> buscar(String nombre_competencia) {
        return competenciaDAO.buscar_por_nombre(nombre_competencia);
    }
}
