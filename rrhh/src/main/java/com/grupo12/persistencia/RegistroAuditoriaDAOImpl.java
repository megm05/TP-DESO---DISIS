package com.grupo12.persistencia;

import com.grupo12.dominio.Auditoria;
import com.grupo12.dominio.Registro_pregunta;

/** Implementación JDBC (PostgreSQL) de {@link RegistroAuditoriaDAO}. */
public class RegistroAuditoriaDAOImpl extends DAOBase implements RegistroAuditoriaDAO {

    @Override
    public void guardar(Auditoria registro) {
        if (registro instanceof Registro_pregunta) {
            Registro_pregunta r = (Registro_pregunta) registro;
            int id = insertar("INSERT INTO Registro_pregunta (id_consultor, nombre_borrado, fecha_hora)"
                    + " VALUES (?, ?, ?)", "id_registro",
                    r.getConsultor().getId_consultor(), r.getNombre_borrado(), r.getFecha_hora());
            r.setId_registro(id);
        } else {
            // los registros de factores, competencias, puestos y opciones de respuesta
            // corresponden a casos de uso que no forman parte de esta entrega
            throw new PersistenciaException("Tipo de registro de auditoría no implementado: "
                    + registro.getClass().getSimpleName());
        }
    }
}
