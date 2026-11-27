package com.grupo12.persistencia;

import com.grupo12.dominio.Caracteristicas_del_puesto;
import com.grupo12.dominio.Competencia;
import com.grupo12.dominio.Empresa;
import com.grupo12.dominio.Puesto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Implementación JDBC (PostgreSQL) de PuestoDAO. */
public class PuestoDAOImpl extends DAOBase implements PuestoDAO {

    private static final String SELECT = "SELECT pu.id_puesto, pu.nombre, pu.descripcion,"
            + " e.id_empresa, e.nombre AS empresa_nombre"
            + " FROM Puesto pu JOIN Empresa e ON e.id_empresa = pu.id_empresa";

    @Override
    public Puesto obtener_por_id(int id) {
        List<Puesto> lista = cargar(SELECT + " WHERE pu.id_puesto = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Puesto> buscar_por_filtros(Integer codigo, String nombre, String empresa) {
        StringBuilder sql = new StringBuilder(SELECT + " WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (codigo != null) {
            sql.append(" AND pu.id_puesto = ?");
            parametros.add(codigo);
        }
        if (tieneTexto(nombre)) {
            sql.append(" AND UPPER(pu.nombre) LIKE ? ESCAPE '\\'");
            parametros.add(contiene(nombre));
        }
        if (tieneTexto(empresa)) {
            sql.append(" AND UPPER(e.nombre) LIKE ? ESCAPE '\\'");
            parametros.add(contiene(empresa));
        }
        sql.append(" ORDER BY pu.nombre");
        return cargar(sql.toString(), parametros.toArray());
    }

    private List<Puesto> cargar(String sql, Object... parametros) {
        List<Puesto> puestos = consultar(sql, rs -> {
            Puesto puesto = new Puesto();
            puesto.setId_puesto(rs.getInt("id_puesto"));
            puesto.setNombre(rs.getString("nombre"));
            puesto.setDescripcion(rs.getString("descripcion"));
            Empresa empresa = new Empresa();
            empresa.setId_empresa(rs.getInt("id_empresa"));
            empresa.setNombre(rs.getString("empresa_nombre"));
            puesto.setEmpresa(empresa);
            return puesto;
        }, parametros);
        cargarCaracteristicas(puestos);
        return puestos;
    }

    private void cargarCaracteristicas(List<Puesto> puestos) {
        if (puestos.isEmpty()) {
            return;
        }
        Map<Integer, Puesto> porId = new HashMap<>();
        List<Object> ids = new ArrayList<>();
        for (Puesto puesto : puestos) {
            porId.put(puesto.getId_puesto(), puesto);
            ids.add(puesto.getId_puesto());
        }
        // filas (id_puesto, id_competencia, ponderacion_minima)
        List<double[]> filas = consultar("SELECT id_puesto, id_competencia, ponderacion_minima"
                + " FROM Puesto_Competencia WHERE id_puesto IN (" + CargadorCatalogo.marcadores(ids.size())
                + ") ORDER BY id_puesto, id_competencia",
                rs -> new double[] {rs.getInt("id_puesto"), rs.getInt("id_competencia"),
                    rs.getDouble("ponderacion_minima")},
                ids.toArray());
        if (filas.isEmpty()) {
            return;
        }
        Set<Object> idsCompetencia = new LinkedHashSet<>();
        for (double[] fila : filas) {
            idsCompetencia.add((int) fila[1]);
        }
        Map<Integer, Competencia> competencias = new HashMap<>();
        for (Competencia competencia : new CargadorCatalogo().competencias(
                "c.id_competencia IN (" + CargadorCatalogo.marcadores(idsCompetencia.size()) + ")",
                idsCompetencia.toArray())) {
            competencias.put(competencia.getId_competencia(), competencia);
        }
        for (double[] fila : filas) {
            porId.get((int) fila[0]).getCaracteristicas()
                    .add(new Caracteristicas_del_puesto(competencias.get((int) fila[1]), fila[2]));
        }
    }
}
