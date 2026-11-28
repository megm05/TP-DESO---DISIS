package com.grupo24.persistencia;

import com.grupo24.dominio.Consultor;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Implementación JDBC (PostgreSQL) de ConsultorDAO. */
public class ConsultorDAOImpl extends DAOBase implements ConsultorDAO {

    private static final String SELECT =
            "SELECT id_consultor, nombre_usuario, contrasenia, nombre, apellido FROM Consultor";

    @Override
    public Consultor obtener_por_id(int id) {
        return consultarUno(SELECT + " WHERE id_consultor = ?", ConsultorDAOImpl::mapear, id);
    }

    @Override
    public Consultor obtener_por_nombre_usuario(String nombre_usuario) {
        return consultarUno(SELECT + " WHERE UPPER(nombre_usuario) = ?", ConsultorDAOImpl::mapear,
                nombre_usuario == null ? "" : nombre_usuario.trim().toUpperCase());
    }

    static Consultor mapear(ResultSet rs) throws SQLException {
        Consultor consultor = new Consultor();
        consultor.setId_consultor(rs.getInt("id_consultor"));
        consultor.setNombre_usuario(rs.getString("nombre_usuario"));
        consultor.setContrasenia(rs.getString("contrasenia"));
        consultor.setNombre(rs.getString("nombre"));
        consultor.setApellido(rs.getString("apellido"));
        return consultor;
    }
}
