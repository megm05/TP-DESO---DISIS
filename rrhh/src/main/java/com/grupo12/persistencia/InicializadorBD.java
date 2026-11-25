package com.grupo12.persistencia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Crea el esquema y carga los datos iniciales la primera vez que se ejecuta el
 * sistema contra una base vacía. Los scripts son los mismos que se entregan en
 * la carpeta bd del proyecto (schema.sql y datos_iniciales.sql).
 */
public final class InicializadorBD {

    private static final String[] TABLAS = {
        "Renglon_Orden_merito", "Orden_merito_Evaluacion", "Orden_merito", "Reporte_comparativo",
        "Registro_pregunta", "Registro_factor", "Registro_competencia", "Registro_puesto",
        "Registro_opcion_respuesta", "Registro_ejecucion", "Resultado_factor", "Resultado_competencia",
        "Respuesta", "Bloque_pregunta", "Cuestionario", "Evaluacion", "Ponderacion_opcion", "Pregunta",
        "Detalle_opcion", "Opcion_respuesta", "Factor", "Puesto_Competencia", "Competencia", "Puesto",
        "Empresa", "Candidato", "Consultor", "Parametro"
    };

    private InicializadorBD() {
    }

    /** Indica si las tablas del sistema ya existen en la base. */
    public static boolean esquemaCreado() {
        String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE LOWER(table_name) = 'parametro'";
        try (Statement st = ConexionBD.obtener().createStatement(); ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo verificar el esquema: " + e.getMessage(), e);
        }
    }

    public static void crearEsquema() {
        ejecutarScript("/bd/schema.sql");
    }

    public static void cargarDatosIniciales() {
        ejecutarScript("/bd/datos_iniciales.sql");
    }

    /** Elimina todas las tablas del sistema (para volver a empezar con la base en cero). */
    public static void eliminarEsquema() {
        try (Statement st = ConexionBD.obtener().createStatement()) {
            for (String tabla : TABLAS) {
                st.executeUpdate("DROP TABLE IF EXISTS " + tabla + " CASCADE");
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el esquema: " + e.getMessage(), e);
        }
    }

    private static void ejecutarScript(String recurso) {
        List<String> sentencias = leerSentencias(recurso);
        Transaccion.realizar(() -> {
            Connection conexion = ConexionBD.obtener();
            try (Statement st = conexion.createStatement()) {
                for (String sentencia : sentencias) {
                    st.executeUpdate(sentencia);
                }
            } catch (SQLException e) {
                throw new PersistenciaException("Error al ejecutar " + recurso + ": " + e.getMessage(), e);
            }
        });
    }

    /** Separa el script en sentencias, ignorando comentarios y respetando los textos entre comillas. */
    static List<String> leerSentencias(String recurso) {
        StringBuilder texto = new StringBuilder();
        try (InputStream in = InicializadorBD.class.getResourceAsStream(recurso)) {
            if (in == null) {
                throw new PersistenciaException("No se encontró el script " + recurso);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.trim().startsWith("--")) {
                    texto.append(linea).append('\n');
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer el script " + recurso, e);
        }
        List<String> sentencias = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            if (c == '\'') {
                entreComillas = !entreComillas;
            }
            if (c == ';' && !entreComillas) {
                if (actual.toString().trim().length() > 0) {
                    sentencias.add(actual.toString().trim());
                }
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (actual.toString().trim().length() > 0) {
            sentencias.add(actual.toString().trim());
        }
        return sentencias;
    }
}
