package com.grupo12.persistencia;

import com.grupo12.dominio.Competencia;
import com.grupo12.dominio.Detalle_opcion;
import com.grupo12.dominio.Factor;
import com.grupo12.dominio.Opcion_respuesta;
import com.grupo12.dominio.Ponderacion_opcion;
import com.grupo12.dominio.Pregunta;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Colaborador interno de los DAO: arma, con pocas consultas, el árbol
 * competencia → factores → preguntas → opción de respuesta y ponderaciones.
 * Se crea una instancia por cada carga.
 */
final class CargadorCatalogo extends DAOBase {

    private static final String COLUMNAS_COMPETENCIA = "c.id_competencia, c.nombre AS competencia_nombre, c.tipo";
    private static final String COLUMNAS_FACTOR =
            "f.id_factor, f.nombre AS factor_nombre, f.descripcion AS factor_descripcion";
    private static final String COLUMNAS_PREGUNTA =
            "p.id_pregunta, p.id_factor AS pregunta_id_factor, p.id_opcion, p.nombre AS pregunta_nombre, "
            + "p.pregunta, p.descripcion AS pregunta_descripcion, p.activo";

    private final Map<Integer, Competencia> competencias = new LinkedHashMap<>();
    private final Map<Integer, Factor> factores = new LinkedHashMap<>();
    private final Map<Integer, Integer> opcionPorPregunta = new HashMap<>();

    /** Competencias que cumplen la condición (sobre el alias c), con sus factores y preguntas. */
    List<Competencia> competencias(String condicion, Object... parametros) {
        List<Competencia> lista = consultar("SELECT " + COLUMNAS_COMPETENCIA + " FROM Competencia c"
                + donde(condicion) + " ORDER BY c.nombre", this::competencia, parametros);
        if (lista.isEmpty()) {
            return lista;
        }
        List<Factor> susFactores = consultar("SELECT " + COLUMNAS_FACTOR + ", f.id_competencia FROM Factor f"
                + " WHERE f.id_competencia IN (" + marcadores(competencias.size()) + ") ORDER BY f.nombre",
                rs -> {
                    Factor factor = factor(rs);
                    Competencia competencia = competencias.get(rs.getInt("id_competencia"));
                    factor.setCompetencia(competencia);
                    competencia.obtener_factores().add(factor);
                    return factor;
                }, competencias.keySet().toArray());
        cargarPreguntasDe(susFactores);
        return lista;
    }

    /** Factores que cumplen la condición (sobre los alias f y c), con su competencia y sus preguntas. */
    List<Factor> factores(String condicion, Object... parametros) {
        List<Factor> lista = consultar("SELECT " + COLUMNAS_FACTOR + ", " + COLUMNAS_COMPETENCIA
                + " FROM Factor f JOIN Competencia c ON c.id_competencia = f.id_competencia"
                + donde(condicion) + " ORDER BY f.nombre",
                rs -> {
                    Factor factor = factor(rs);
                    factor.setCompetencia(competencia(rs));
                    return factor;
                }, parametros);
        cargarPreguntasDe(lista);
        return lista;
    }

    /**
     * Preguntas que cumplen la condición (sobre los alias p, f y c), con su factor,
     * su competencia, su opción de respuesta y sus ponderaciones.
     */
    List<Pregunta> preguntas(String condicion, Object... parametros) {
        List<Pregunta> lista = consultar("SELECT " + COLUMNAS_PREGUNTA + ", " + COLUMNAS_FACTOR + ", "
                + COLUMNAS_COMPETENCIA + " FROM Pregunta p"
                + " JOIN Factor f ON f.id_factor = p.id_factor"
                + " JOIN Competencia c ON c.id_competencia = f.id_competencia"
                + donde(condicion) + " ORDER BY c.nombre, f.nombre, p.nombre",
                rs -> {
                    Pregunta pregunta = pregunta(rs);
                    Factor factor = factor(rs);
                    if (factor.getCompetencia() == null) {
                        factor.setCompetencia(competencia(rs));
                    }
                    pregunta.setFactor(factor);
                    return pregunta;
                }, parametros);
        completar(lista);
        return lista;
    }

    private void cargarPreguntasDe(List<Factor> susFactores) {
        if (susFactores.isEmpty()) {
            return;
        }
        Map<Integer, Factor> porId = new HashMap<>();
        List<Object> ids = new ArrayList<>();
        for (Factor factor : susFactores) {
            porId.put(factor.getId_factor(), factor);
            ids.add(factor.getId_factor());
        }
        List<Pregunta> lista = consultar("SELECT " + COLUMNAS_PREGUNTA + " FROM Pregunta p"
                + " WHERE p.id_factor IN (" + marcadores(ids.size()) + ") ORDER BY p.nombre",
                rs -> {
                    Pregunta pregunta = pregunta(rs);
                    Factor factor = porId.get(rs.getInt("pregunta_id_factor"));
                    pregunta.setFactor(factor);
                    factor.getPreguntas().add(pregunta);
                    return pregunta;
                }, ids.toArray());
        completar(lista);
    }

    /** Asigna a cada pregunta su opción de respuesta y las ponderaciones de sus opciones. */
    private void completar(List<Pregunta> lista) {
        if (lista.isEmpty()) {
            return;
        }
        Map<Integer, Opcion_respuesta> opciones = new HashMap<>();
        for (Opcion_respuesta opcion : new OpcionRespuestaDAOImpl().obtener_todos()) {
            opciones.put(opcion.getId_opcion(), opcion);
        }
        Map<Integer, Pregunta> porId = new HashMap<>();
        List<Object> ids = new ArrayList<>();
        for (Pregunta pregunta : lista) {
            pregunta.setOpcion_respuesta(opciones.get(opcionPorPregunta.get(pregunta.getId_pregunta())));
            porId.put(pregunta.getId_pregunta(), pregunta);
            ids.add(pregunta.getId_pregunta());
        }
        consultar("SELECT id_pregunta, id_detalle, ponderacion FROM Ponderacion_opcion"
                + " WHERE id_pregunta IN (" + marcadores(ids.size()) + ")",
                rs -> {
                    Pregunta pregunta = porId.get(rs.getInt("id_pregunta"));
                    int id_detalle = rs.getInt("id_detalle");
                    for (Detalle_opcion detalle : pregunta.getOpcion_respuesta().obtener_detalles()) {
                        if (detalle.getId_detalle() == id_detalle) {
                            pregunta.getPonderaciones()
                                    .add(new Ponderacion_opcion(detalle, rs.getDouble("ponderacion")));
                        }
                    }
                    return null;
                }, ids.toArray());
        for (Pregunta pregunta : lista) {
            pregunta.getPonderaciones().sort((a, b) ->
                    Integer.compare(a.getDetalle().getOrden(), b.getDetalle().getOrden()));
        }
    }

    // ------------------------------------------------------------------
    //  Conversión de filas a objetos (una única instancia por identificador)
    // ------------------------------------------------------------------

    private Competencia competencia(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_competencia");
        Competencia competencia = competencias.get(id);
        if (competencia == null) {
            competencia = new Competencia();
            competencia.setId_competencia(id);
            competencia.setNombre(rs.getString("competencia_nombre"));
            competencia.setTipo(rs.getString("tipo"));
            competencias.put(id, competencia);
        }
        return competencia;
    }

    private Factor factor(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_factor");
        Factor factor = factores.get(id);
        if (factor == null) {
            factor = new Factor();
            factor.setId_factor(id);
            factor.setNombre(rs.getString("factor_nombre"));
            factor.setDescripcion(rs.getString("factor_descripcion"));
            factores.put(id, factor);
        }
        return factor;
    }

    private Pregunta pregunta(ResultSet rs) throws SQLException {
        Pregunta pregunta = new Pregunta();
        pregunta.setId_pregunta(rs.getInt("id_pregunta"));
        pregunta.setNombre(rs.getString("pregunta_nombre"));
        pregunta.setPregunta(rs.getString("pregunta"));
        pregunta.setDescripcion(rs.getString("pregunta_descripcion"));
        pregunta.setActivo(rs.getBoolean("activo"));
        opcionPorPregunta.put(pregunta.getId_pregunta(), rs.getInt("id_opcion"));
        return pregunta;
    }

    private static String donde(String condicion) {
        return condicion == null || condicion.isEmpty() ? "" : " WHERE " + condicion;
    }

    static String marcadores(int cantidad) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cantidad; i++) {
            sb.append(i == 0 ? "?" : ", ?");
        }
        return sb.toString();
    }
}
