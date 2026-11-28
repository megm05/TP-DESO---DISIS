package com.grupo24.gestores;

import com.grupo24.dominio.Factor;
import com.grupo24.dto.FactorDTO;
import com.grupo24.persistencia.FabricaDAO;
import com.grupo24.persistencia.FactorDAO;
import java.util.ArrayList;
import java.util.List;

/** Gestor de factores. */
public class Gestor_de_factor {

    private final FactorDAO factorDAO;

    public Gestor_de_factor() {
        this(FabricaDAO.getFactorDAO());
    }

    public Gestor_de_factor(FactorDAO factorDAO) {
        this.factorDAO = factorDAO;
    }

    /** Factores de la competencia seleccionada (CU021: al elegir la competencia se cargan sus factores). */
    public List<FactorDTO> obtener_por_competencia(int id_competencia) {
        List<FactorDTO> resultado = new ArrayList<>();
        for (Factor factor : factorDAO.obtener_por_competencia(id_competencia)) {
            resultado.add(new FactorDTO(factor.getId_factor(), factor.getNombre()));
        }
        return resultado;
    }
}
