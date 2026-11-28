package com.grupo24.gestores;

import com.grupo24.dominio.Competencia;
import com.grupo24.dto.CompetenciaDTO;
import com.grupo24.persistencia.CompetenciaDAO;
import com.grupo24.persistencia.FabricaDAO;
import java.util.ArrayList;
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

    /** Competencias para la lista de selección del alta de preguntas (CU021 paso 1). */
    public List<CompetenciaDTO> buscar(String nombre_competencia) {
        List<CompetenciaDTO> resultado = new ArrayList<>();
        for (Competencia competencia : competenciaDAO.buscar_por_nombre(nombre_competencia)) {
            resultado.add(new CompetenciaDTO(competencia.getId_competencia(), competencia.getNombre(),
                    competencia.getTipo()));
        }
        return resultado;
    }
}
