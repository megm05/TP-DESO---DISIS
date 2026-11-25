package com.grupo12.persistencia;

import com.grupo12.dominio.Detalle_opcion;
import com.grupo12.dominio.Opcion_respuesta;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Implementación JDBC (PostgreSQL) de {@link OpcionRespuestaDAO}. */
public class OpcionRespuestaDAOImpl extends DAOBase implements OpcionRespuestaDAO {

    @Override
    public Opcion_respuesta obtener_por_id(int id) {
        List<Opcion_respuesta> lista = cargar(" WHERE o.id_opcion = ?", id);
        return lista.isEmpty() ? null : lista.get(0);
    }

    @Override
    public List<Opcion_respuesta> obtener_todos() {
        return cargar("");
    }

    private List<Opcion_respuesta> cargar(String condicion, Object... parametros) {
        List<Opcion_respuesta> opciones = consultar(
                "SELECT o.id_opcion, o.nombre, o.descripcion FROM Opcion_respuesta o" + condicion
                + " ORDER BY o.nombre",
                rs -> {
                    Opcion_respuesta opcion = new Opcion_respuesta();
                    opcion.setId_opcion(rs.getInt("id_opcion"));
                    opcion.setNombre(rs.getString("nombre"));
                    opcion.setDescripcion(rs.getString("descripcion"));
                    return opcion;
                }, parametros);
        if (opciones.isEmpty()) {
            return opciones;
        }
        Map<Integer, List<Detalle_opcion>> detalles = new HashMap<>();
        List<Object> ids = new ArrayList<>();
        for (Opcion_respuesta opcion : opciones) {
            detalles.put(opcion.getId_opcion(), new ArrayList<>());
            ids.add(opcion.getId_opcion());
        }
        consultar("SELECT id_detalle, id_opcion, orden, descripcion FROM Detalle_opcion"
                + " WHERE id_opcion IN (" + CargadorCatalogo.marcadores(ids.size()) + ") ORDER BY orden",
                rs -> detalles.get(rs.getInt("id_opcion")).add(new Detalle_opcion(
                        rs.getInt("id_detalle"), rs.getInt("orden"), rs.getString("descripcion"))),
                ids.toArray());
        for (Opcion_respuesta opcion : opciones) {
            opcion.setDetalles(detalles.get(opcion.getId_opcion()));
        }
        return opciones;
    }
}
