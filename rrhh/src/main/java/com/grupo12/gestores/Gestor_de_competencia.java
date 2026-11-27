package com.grupo12.gestores;

import com.grupo12.dominio.Competencia;
import com.grupo12.persistencia.CompetenciaDAO;
import com.grupo12.persistencia.FabricaDAO;
import java.util.List;

/** Gestor de competencias. */
public class Gestor_de_competencia {

    private final CompetenciaDAO competenciaDAO;

    public Gestor_de_competencia() {
        this(FabricaDAO.getCompetenciaDAO());
    }

    public Gestor_de_competencia(CompetenciaDAO competenciaDAO) {
        this.competenciaDAO = competenciaDAO;
    }

    public List<Competencia> buscar(String nombre_competencia) {
        return competenciaDAO.buscar_por_nombre(nombre_competencia);
    }
}
