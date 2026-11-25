package com.grupo12.persistencia;

import com.grupo12.dominio.Factor;
import java.util.List;

/** Implementación JDBC (PostgreSQL) de {@link FactorDAO}. */
public class FactorDAOImpl extends DAOBase implements FactorDAO {

    @Override
    public Factor obtener_por_id(int id) {
        List<Factor> lista = new CargadorCatalogo().factores("f.id_factor = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Factor> obtener_por_competencia(int id_competencia) {
        return new CargadorCatalogo().factores("f.id_competencia = ?", id_competencia);
    }
}
