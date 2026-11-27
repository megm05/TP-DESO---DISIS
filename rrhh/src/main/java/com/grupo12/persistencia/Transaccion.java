package com.grupo12.persistencia;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Demarca transacciones sobre la conexión del hilo actual: todo lo que el gestor ejecute dentro de una
 * transacción se confirma junto o no se confirma.
 */
public final class Transaccion {

    /** Operación con resultado que se ejecuta dentro de una transacción. */
    public interface Operacion<T> {
        T ejecutar();
    }

    private Transaccion() {
    }

    public static <T> T ejecutar(Operacion<T> operacion) {
        Connection conexion = ConexionBD.obtener();
        boolean propia;
        try {
            // si ya hay una transacción en curso, esta operación se suma a ella
            propia = conexion.getAutoCommit();
            if (propia) {
                conexion.setAutoCommit(false);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo iniciar la transacción", e);
        }
        try {
            T resultado = operacion.ejecutar();
            if (propia) {
                conexion.commit();
            }
            return resultado;
        } catch (RuntimeException e) {
            if (propia) {
                deshacer(conexion);
            }
            throw e;
        } catch (SQLException e) {
            deshacer(conexion);
            throw new PersistenciaException("No se pudo confirmar la transacción", e);
        } finally {
            if (propia) {
                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    // la conexión se descarta al cerrar el pedido
                }
            }
        }
    }

    public static void realizar(Runnable accion) {
        ejecutar(() -> {
            accion.run();
            return null;
        });
    }

    private static void deshacer(Connection conexion) {
        try {
            conexion.rollback();
        } catch (SQLException e) {
            // se informa el error original
        }
    }
}
