package com.grupo12.persistencia;

import com.grupo12.dominio.Competencia;
import com.grupo12.dominio.Factor;
import com.grupo12.dominio.Ponderacion_opcion;
import com.grupo12.dominio.Pregunta;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC (PostgreSQL) de {@link PreguntaDAO}. */
public class PreguntaDAOImpl extends DAOBase implements PreguntaDAO {

    @Override
    public Pregunta obtener_por_id(int id) {
        List<Pregunta> lista = new CargadorCatalogo().preguntas("p.id_pregunta = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public void guardar(Pregunta pregunta) {
        int id = insertar("INSERT INTO Pregunta (id_factor, id_opcion, nombre, pregunta, descripcion, activo)"
                + " VALUES (?, ?, ?, ?, ?, ?)", "id_pregunta",
                pregunta.getFactor().getId_factor(),
                pregunta.getOpcion_respuesta().getId_opcion(),
                pregunta.getNombre(), pregunta.getPregunta(), pregunta.getDescripcion(), pregunta.isActivo());
        pregunta.setId_pregunta(id);
        guardarPonderaciones(pregunta);
    }

    @Override
    public void actualizar(Pregunta pregunta) {
        ejecutar("UPDATE Pregunta SET id_opcion = ?, nombre = ?, pregunta = ?, descripcion = ?, activo = ?"
                + " WHERE id_pregunta = ?",
                pregunta.getOpcion_respuesta().getId_opcion(),
                pregunta.getNombre(), pregunta.getPregunta(), pregunta.getDescripcion(), pregunta.isActivo(),
                pregunta.getId_pregunta());
        ejecutar("DELETE FROM Ponderacion_opcion WHERE id_pregunta = ?", pregunta.getId_pregunta());
        guardarPonderaciones(pregunta);
    }

    private void guardarPonderaciones(Pregunta pregunta) {
        for (Ponderacion_opcion ponderacion : pregunta.getPonderaciones()) {
            ejecutar("INSERT INTO Ponderacion_opcion (id_pregunta, id_detalle, ponderacion) VALUES (?, ?, ?)",
                    pregunta.getId_pregunta(), ponderacion.getDetalle().getId_detalle(),
                    ponderacion.getPonderacion());
        }
    }

    @Override
    public List<Pregunta> buscar_por_filtros(String nombre_competencia, String nombre_factor,
            String nombre_pregunta) {
        StringBuilder condicion = new StringBuilder("p.activo = TRUE");
        List<Object> parametros = new ArrayList<>();
        if (tieneTexto(nombre_competencia)) {
            condicion.append(" AND UPPER(c.nombre) LIKE ? ESCAPE '\\'");
            parametros.add(contiene(nombre_competencia));
        }
        if (tieneTexto(nombre_factor)) {
            condicion.append(" AND UPPER(f.nombre) LIKE ? ESCAPE '\\'");
            parametros.add(contiene(nombre_factor));
        }
        if (tieneTexto(nombre_pregunta)) {
            condicion.append(" AND UPPER(p.nombre) LIKE ? ESCAPE '\\'");
            parametros.add(contiene(nombre_pregunta));
        }
        return new CargadorCatalogo().preguntas(condicion.toString(), parametros.toArray());
    }

    @Override
    public List<Pregunta> obtener_activas_por_factor(Factor factor) {
        List<Pregunta> preguntas = new CargadorCatalogo()
                .preguntas("p.id_factor = ? AND p.activo = TRUE", factor.getId_factor());
        for (Pregunta pregunta : preguntas) {
            pregunta.setFactor(factor);
        }
        return preguntas;
    }

    @Override
    public boolean existe_pregunta(String nombre, String pregunta, Competencia competencia, Factor factor,
            int id_excluida) {
        List<String> criterios = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        parametros.add(factor.getId_factor());
        parametros.add(competencia.getId_competencia());
        parametros.add(id_excluida);
        if (nombre != null) {
            criterios.add("UPPER(p.nombre) = ?");
            parametros.add(nombre.trim().toUpperCase());
        }
        if (pregunta != null) {
            criterios.add("UPPER(p.pregunta) = ?");
            parametros.add(pregunta.trim().toUpperCase());
        }
        if (criterios.isEmpty()) {
            return false;
        }
        return contar("SELECT COUNT(*) FROM Pregunta p JOIN Factor f ON f.id_factor = p.id_factor"
                + " WHERE p.activo = TRUE AND p.id_factor = ? AND f.id_competencia = ? AND p.id_pregunta <> ?"
                + " AND (" + String.join(" OR ", criterios) + ")", parametros.toArray()) > 0;
    }
}
