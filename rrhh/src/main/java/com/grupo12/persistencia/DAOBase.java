package com.grupo12.persistencia;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Funcionalidad común de las implementaciones JDBC de los DAO: ejecución de
 * sentencias con parámetros y conversión de tipos.
 */
abstract class DAOBase {

    /** Convierte la fila actual de un resultado en un objeto. */
    protected interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    protected <T> List<T> consultar(String sql, Mapeador<T> mapeador, Object... parametros) {
        try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> resultado = new ArrayList<>();
                while (rs.next()) {
                    resultado.add(mapeador.mapear(rs));
                }
                return resultado;
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al consultar la base de datos: " + e.getMessage(), e);
        }
    }

    protected <T> T consultarUno(String sql, Mapeador<T> mapeador, Object... parametros) {
        List<T> resultado = consultar(sql, mapeador, parametros);
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    protected int contar(String sql, Object... parametros) {
        Integer cantidad = consultarUno(sql, rs -> rs.getInt(1), parametros);
        return cantidad == null ? 0 : cantidad;
    }

    protected int ejecutar(String sql, Object... parametros) {
        try (PreparedStatement ps = ConexionBD.obtener().prepareStatement(sql)) {
            asignar(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException("Error al actualizar la base de datos: " + e.getMessage(), e);
        }
    }

    /** Ejecuta un INSERT y devuelve el identificador generado por la base. */
    protected int insertar(String sql, String columnaId, Object... parametros) {
        Connection conexion = ConexionBD.obtener();
        try (PreparedStatement ps = conexion.prepareStatement(sql, new String[] {columnaId})) {
            asignar(ps, parametros);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    return claves.getInt(1);
                }
                throw new PersistenciaException("La base de datos no devolvió el identificador generado");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("Error al insertar en la base de datos: " + e.getMessage(), e);
        }
    }

    private static void asignar(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object valor = parametros[i];
            if (valor instanceof LocalDateTime) {
                valor = Timestamp.valueOf((LocalDateTime) valor);
            } else if (valor instanceof LocalDate) {
                valor = Date.valueOf((LocalDate) valor);
            } else if (valor instanceof Enum) {
                valor = ((Enum<?>) valor).name();
            } else if (valor instanceof Character) {
                valor = valor.toString();
            }
            ps.setObject(i + 1, valor);
        }
    }

    protected static LocalDateTime fechaHora(ResultSet rs, String columna) throws SQLException {
        Timestamp valor = rs.getTimestamp(columna);
        return valor == null ? null : valor.toLocalDateTime();
    }

    protected static LocalDate fecha(ResultSet rs, String columna) throws SQLException {
        Date valor = rs.getDate(columna);
        return valor == null ? null : valor.toLocalDate();
    }

    protected static Integer enteroONulo(ResultSet rs, String columna) throws SQLException {
        int valor = rs.getInt(columna);
        return rs.wasNull() ? null : valor;
    }

    protected static Double decimalONulo(ResultSet rs, String columna) throws SQLException {
        double valor = rs.getDouble(columna);
        return rs.wasNull() ? null : valor;
    }

    /** Patrón para búsquedas por "contiene", sin distinguir mayúsculas de minúsculas. */
    protected static String contiene(String texto) {
        String limpio = texto.trim().toUpperCase()
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + limpio + "%";
    }

    protected static boolean tieneTexto(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
}
