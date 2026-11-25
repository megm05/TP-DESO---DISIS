package com.grupo12.persistencia;

import com.grupo12.util.Configuracion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Administra la conexión a la base de datos. Cada hilo (cada pedido que atiende
 * el servidor) trabaja con su propia conexión, que se abre al primer uso y se
 * cierra al terminar de atender el pedido.
 */
public final class ConexionBD {

    private static final ThreadLocal<Connection> ACTUAL = new ThreadLocal<>();

    private ConexionBD() {
    }

    /** Conexión del hilo actual; la abre si todavía no existe. */
    public static Connection obtener() {
        try {
            Connection conexion = ACTUAL.get();
            if (conexion == null || conexion.isClosed()) {
                String url = Configuracion.get(Configuracion.DB_URL);
                registrarDriver(url);
                conexion = DriverManager.getConnection(url,
                        Configuracion.get(Configuracion.DB_USUARIO),
                        Configuracion.get(Configuracion.DB_CLAVE));
                ACTUAL.set(conexion);
            }
            return conexion;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
    }

    /** Cierra la conexión del hilo actual (si hay una abierta). */
    public static void cerrar() {
        Connection conexion = ACTUAL.get();
        ACTUAL.remove();
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                // no hay nada más que hacer con una conexión que no se pudo cerrar
            }
        }
    }

    private static void registrarDriver(String url) {
        String driver = url.startsWith("jdbc:h2:") ? "org.h2.Driver" : "org.postgresql.Driver";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new PersistenciaException("No se encontró el driver JDBC " + driver
                    + ". Verifique que las librerías de la carpeta lib estén disponibles.");
        }
    }
}
