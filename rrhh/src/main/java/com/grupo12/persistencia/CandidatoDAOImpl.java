package com.grupo12.persistencia;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Tipo_doc;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Implementación JDBC (PostgreSQL) de {@link CandidatoDAO}. */
public class CandidatoDAOImpl extends DAOBase implements CandidatoDAO {

    /** Columnas del candidato con el alias de tabla "ca", para reutilizar en otros DAO. */
    static final String COLUMNAS = "ca.id_candidato, ca.num_candidato, ca.tipo_doc, ca.nro_doc,"
            + " ca.nombre AS candidato_nombre, ca.apellido AS candidato_apellido, ca.email, ca.genero,"
            + " ca.fecha_nac, ca.escolaridad";

    private static final String SELECT = "SELECT " + COLUMNAS + " FROM Candidato ca";

    @Override
    public Candidato obtener_por_id(int id) {
        return consultarUno(SELECT + " WHERE ca.id_candidato = ?", CandidatoDAOImpl::mapear, id);
    }

    @Override
    public Candidato obtener_por_numero(int num_candidato) {
        return consultarUno(SELECT + " WHERE ca.num_candidato = ?", CandidatoDAOImpl::mapear, num_candidato);
    }

    @Override
    public Candidato obtener_por_documento(Tipo_doc tipo_doc, String nro_doc) {
        return consultarUno(SELECT + " WHERE ca.tipo_doc = ? AND UPPER(ca.nro_doc) = ?",
                CandidatoDAOImpl::mapear, tipo_doc, nro_doc == null ? "" : nro_doc.trim().toUpperCase());
    }

    @Override
    public List<Candidato> buscar_por_filtros(String apellido, String nombre, Integer num_candidato) {
        StringBuilder sql = new StringBuilder(SELECT + " WHERE 1 = 1");
        List<Object> parametros = new ArrayList<>();
        if (tieneTexto(apellido)) {
            sql.append(" AND UPPER(ca.apellido) LIKE ? ESCAPE '\\'");
            parametros.add(contiene(apellido));
        }
        if (tieneTexto(nombre)) {
            sql.append(" AND UPPER(ca.nombre) LIKE ? ESCAPE '\\'");
            parametros.add(contiene(nombre));
        }
        if (num_candidato != null) {
            sql.append(" AND ca.num_candidato = ?");
            parametros.add(num_candidato);
        }
        sql.append(" ORDER BY ca.apellido, ca.nombre");
        return consultar(sql.toString(), CandidatoDAOImpl::mapear, parametros.toArray());
    }

    static Candidato mapear(ResultSet rs) throws SQLException {
        Candidato candidato = new Candidato();
        candidato.setId_candidato(rs.getInt("id_candidato"));
        candidato.setNum_candidato(rs.getInt("num_candidato"));
        candidato.setTipo_doc(Tipo_doc.valueOf(rs.getString("tipo_doc").trim()));
        candidato.setNro_doc(rs.getString("nro_doc"));
        candidato.setNombre(rs.getString("candidato_nombre"));
        candidato.setApellido(rs.getString("candidato_apellido"));
        candidato.setEmail(rs.getString("email"));
        String genero = rs.getString("genero");
        candidato.setGenero(genero == null || genero.trim().isEmpty() ? ' ' : genero.trim().charAt(0));
        candidato.setFecha_nac(fecha(rs, "fecha_nac"));
        candidato.setEscolaridad(rs.getString("escolaridad"));
        return candidato;
    }
}
