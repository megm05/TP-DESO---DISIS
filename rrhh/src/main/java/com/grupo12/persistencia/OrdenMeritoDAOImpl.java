package com.grupo12.persistencia;

import com.grupo12.dominio.Evaluacion;
import com.grupo12.dominio.OrdenMerito;
import com.grupo12.dominio.Renglon_Orden_Merito;

/** Implementación JDBC (PostgreSQL) de {@link OrdenMeritoDAO}. */
public class OrdenMeritoDAOImpl extends DAOBase implements OrdenMeritoDAO {

    @Override
    public void guardar(OrdenMerito ordenMerito) {
        int id = insertar("INSERT INTO Orden_merito (id_puesto, id_consultor, fecha_generacion)"
                + " VALUES (?, ?, ?)", "id_orden_merito",
                ordenMerito.getPuesto().getId_puesto(), ordenMerito.getConsultor().getId_consultor(),
                ordenMerito.getFecha_generacion());
        ordenMerito.setId_orden_merito(id);
        for (Evaluacion evaluacion : ordenMerito.getEvaluaciones()) {
            ejecutar("INSERT INTO Orden_merito_Evaluacion (id_orden_merito, id_evaluacion) VALUES (?, ?)",
                    id, evaluacion.getId_evaluacion());
        }
        for (Renglon_Orden_Merito renglon : ordenMerito.getRenglones()) {
            ejecutar("INSERT INTO Renglon_Orden_merito (id_orden_merito, nro_renglon, id_candidato,"
                    + " id_cuestionario, posicion, puntaje_obtenido) VALUES (?, ?, ?, ?, ?, ?)",
                    id, renglon.getNro_renglon(), renglon.getCandidato().getId_candidato(),
                    renglon.getCuestionario().getId_cuestionario(), renglon.getPosicion(),
                    renglon.getPuntaje_obtenido());
        }
    }
}
