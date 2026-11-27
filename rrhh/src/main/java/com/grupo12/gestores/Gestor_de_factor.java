package com.grupo12.gestores;

import com.grupo12.dominio.Factor;
import com.grupo12.persistencia.FabricaDAO;
import com.grupo12.persistencia.FactorDAO;
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

    /** Factores correspondientes a una competencia (CU021: al seleccionarla se cargan sus factores). */
    public List<Factor> obtener_por_competencia(int id_competencia) {
        return factorDAO.obtener_por_competencia(id_competencia);
    }
}
