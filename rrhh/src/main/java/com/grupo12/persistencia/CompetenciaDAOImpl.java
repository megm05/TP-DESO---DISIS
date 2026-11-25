package com.grupo12.persistencia;

import com.grupo12.dominio.Competencia;
import java.util.List;

/** Implementación JDBC (PostgreSQL) de {@link CompetenciaDAO}. */
public class CompetenciaDAOImpl extends DAOBase implements CompetenciaDAO {

    @Override
    public Competencia obtener_por_id(int id) {
        List<Competencia> lista = new CargadorCatalogo().competencias("c.id_competencia = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Competencia> buscar_por_nombre(String nombre) {
        if (!tieneTexto(nombre)) {
            return new CargadorCatalogo().competencias("");
        }
        return new CargadorCatalogo().competencias("UPPER(c.nombre) LIKE ? ESCAPE '\\'", contiene(nombre));
    }
}
