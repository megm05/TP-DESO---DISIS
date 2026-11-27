package com.grupo12.persistencia;

import com.grupo12.util.Configuracion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Administra la conexión a la base de datos. */
public final class ConexionBD {

    private static final ThreadLocal<Connection> ACTUAL = new ThreadLocal<>();

    private ConexionBD() {
    }

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
