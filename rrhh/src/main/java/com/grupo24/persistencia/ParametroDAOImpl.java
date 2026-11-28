package com.grupo24.persistencia;

import com.grupo24.dominio.Parametro;

/** Implementación JDBC (PostgreSQL) de ParametroDAO. */
public class ParametroDAOImpl extends DAOBase implements ParametroDAO {

    @Override
    public Parametro obtener_por_clave(String clave) {
        return consultarUno("SELECT clave, valor, descripcion FROM Parametro WHERE clave = ?",
                rs -> {
                    Parametro parametro = new Parametro();
                    parametro.setClave(rs.getString("clave"));
                    parametro.setValor(rs.getString("valor"));
                    parametro.setDescripcion(rs.getString("descripcion"));
                    return parametro;
                }, clave);
    }
}
