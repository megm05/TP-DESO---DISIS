package com.grupo24;

import com.grupo24.persistencia.ConexionBD;
import com.grupo24.persistencia.InicializadorBD;
import com.grupo24.persistencia.PersistenciaException;
import com.grupo24.presentacion.ServidorWeb;
import com.grupo24.util.Configuracion;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/** Punto de entrada del sistema de Gestión de Evaluaciones de Perfiles. */
public final class Aplicacion {

    private Aplicacion() {
    }

    public static void main(String[] args) {
        Configuracion.cargar(args);
        List<String> opciones = Arrays.asList(args);
        System.out.println("==============================================================");
        System.out.println(" Capit@l Humano - Gestión de Evaluaciones de Perfiles");
        System.out.println(" Diseño de Sistemas 2026 - UTN FRSF - Grupo 24");
        System.out.println("==============================================================");
        System.out.println("Base de datos: " + Configuracion.get(Configuracion.DB_URL)
                + (Configuracion.esModoDemo() ? "  (modo demo, base embebida)" : ""));

        if (!prepararBaseDeDatos(opciones.contains("--reiniciar-bd"))) {
            System.exit(1);
            return;
        }

        int puerto = Configuracion.getEntero(Configuracion.PUERTO, 8080);
        ServidorWeb servidor = new ServidorWeb(puerto);
        try {
            servidor.iniciar();
        } catch (IOException e) {
            System.out.println();
            System.out.println("No se pudo iniciar el servidor web en el puerto " + puerto + ": " + e.getMessage());
            System.out.println("Cambie el puerto en config.properties (servidor.puerto) o cierre el programa que lo usa.");
            System.exit(1);
            return;
        }
        String url = "http://localhost:" + servidor.getPuerto() + "/";
        System.out.println();
        System.out.println("Sistema iniciado. Abra en su navegador: " + url);
        System.out.println("Usuarios de prueba:  CONSULTOR / consultor123     ADMIN / admin123");
        System.out.println("Para detenerlo presione Ctrl+C o cierre esta ventana.");
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::detener));
        if (!opciones.contains("--sin-navegador")) {
            abrirNavegador(url);
        }
    }

    private static boolean prepararBaseDeDatos(boolean reiniciar) {
        try {
            if (reiniciar) {
                System.out.println("Reiniciando la base de datos...");
                InicializadorBD.eliminarEsquema();
            }
            if (!InicializadorBD.esquemaCreado()) {
                System.out.println("Creando las tablas...");
                InicializadorBD.crearEsquema();
                if (Configuracion.esModoDemo() || Configuracion.getBooleano(Configuracion.DATOS_DEMO)) {
                    System.out.println("Cargando los datos iniciales de prueba...");
                    InicializadorBD.cargarDatosIniciales();
                }
            }
            return true;
        } catch (PersistenciaException e) {
            System.out.println();
            System.out.println("ERROR: " + e.getMessage());
            System.out.println();
            System.out.println("Verifique que PostgreSQL esté funcionando, que exista la base de datos y que");
            System.out.println("los datos de conexión de config.properties sean correctos.");
            System.out.println("Para probar el sistema sin PostgreSQL ejecútelo en modo demo (opción --demo).");
            return false;
        } finally {
            ConexionBD.cerrar();
        }
    }

    private static void abrirNavegador(String url) {
        try {
            if (!GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception | Error e) {
            // si no se puede abrir el navegador, el usuario usa la dirección indicada
        }
    }
}
