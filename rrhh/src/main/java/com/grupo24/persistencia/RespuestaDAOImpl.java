package com.grupo24.persistencia;

import com.grupo24.dominio.Respuesta;

/** Implementación JDBC (PostgreSQL) de RespuestaDAO. */
public class RespuestaDAOImpl extends DAOBase implements RespuestaDAO {

    @Override
    public void actualizar(Respuesta respuesta) {
        ejecutar("UPDATE Respuesta SET id_detalle = ?, fecha = ? WHERE id_respuesta = ?",
                respuesta.getDetalle_elegido() == null ? null : respuesta.getDetalle_elegido().getId_detalle(),
                respuesta.getFecha(), respuesta.getId_respuesta());
    }
}
