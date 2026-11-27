package com.grupo12.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/** Configuración de la aplicación. */
public final class Configuracion {

    public static final String DB_URL = "db.url";
    public static final String DB_USUARIO = "db.usuario";
    public static final String DB_CLAVE = "db.clave";
    public static final String PUERTO = "servidor.puerto";
    public static final String DATOS_DEMO = "datos.demo";

    public static final String URL_DEMO =
            "jdbc:h2:file:./datos-demo/rrhh;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH";

    private static final Properties PROPIEDADES = new Properties();
    private static boolean modoDemo;

    private Configuracion() {
    }

    public static synchronized void cargar(String[] argumentos) {
        PROPIEDADES.clear();
        try (InputStream in = Configuracion.class.getResourceAsStream("/config-defecto.properties")) {
            if (in != null) {
                try (Reader r = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    PROPIEDADES.load(r);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer la configuración por defecto", e);
        }
        Path externo = Paths.get("config.properties");
        if (Files.isRegularFile(externo)) {
            try (Reader r = Files.newBufferedReader(externo, StandardCharsets.UTF_8)) {
                PROPIEDADES.load(r);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer config.properties", e);
            }
        }
        for (String clave : new String[] {DB_URL, DB_USUARIO, DB_CLAVE, PUERTO, DATOS_DEMO}) {
            String valor = System.getProperty(clave);
            if (valor != null) {
                PROPIEDADES.setProperty(clave, valor);
            }
        }
        modoDemo = false;
        if (argumentos != null) {
            for (String argumento : argumentos) {
                if ("--demo".equalsIgnoreCase(argumento)) {
                    modoDemo = true;
                    PROPIEDADES.setProperty(DB_URL, URL_DEMO);
                    PROPIEDADES.setProperty(DB_USUARIO, "sa");
                    PROPIEDADES.setProperty(DB_CLAVE, "");
                }
            }
        }
    }

    public static String get(String clave) {
        String valor = PROPIEDADES.getProperty(clave);
        return valor == null ? "" : valor.trim();
    }

    public static int getEntero(String clave, int porDefecto) {
        try {
            return Integer.parseInt(get(clave));
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    public static boolean getBooleano(String clave) {
        return "true".equalsIgnoreCase(get(clave)) || "si".equalsIgnoreCase(get(clave));
    }

    public static boolean esModoDemo() {
        return modoDemo;
    }
}
