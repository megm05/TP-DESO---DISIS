package com.grupo12.persistencia;

import com.grupo12.dominio.Respuesta;

/** Implementación JDBC (PostgreSQL) de {@link RespuestaDAO}. */
public class RespuestaDAOImpl extends DAOBase implements RespuestaDAO {

    @Override
    public void actualizar(Respuesta respuesta) {
        ejecutar("UPDATE Respuesta SET id_detalle = ?, fecha = ? WHERE id_respuesta = ?",
                respuesta.getDetalle_elegido() == null ? null : respuesta.getDetalle_elegido().getId_detalle(),
                respuesta.getFecha(), respuesta.getId_respuesta());
    }
}
