package com.grupo12.presentacion;

import com.grupo12.gestores.ValidacionException;
import com.grupo12.persistencia.ConexionBD;
import com.grupo12.persistencia.PersistenciaException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor web embebido (com.sun.net.httpserver, incluido en el JDK). Dirige cada
 * pedido a la pantalla que corresponde según la ruta y garantiza que la conexión
 * a la base de datos usada para atenderlo se cierre al terminar.
 */
public class ServidorWeb {

    private final int puerto;
    private final GestorSesiones sesiones = new GestorSesiones();
    /** Ruta → pantalla. Se elige la entrada con el prefijo más largo que coincida. */
    private final Map<String, Pantalla> rutas = new LinkedHashMap<>();
    private HttpServer servidor;
    private ExecutorService hilos;

    public ServidorWeb(int puerto) {
        this.puerto = puerto;
        PantallaLogin login = new PantallaLogin();
        rutas.put("/login", login);
        rutas.put("/salir", login);
        rutas.put("/principal", new PantallaPrincipal());
        rutas.put("/preguntas", new PantallaGestionPreguntas());
        rutas.put("/preguntas/alta", new PantallaAltaPregunta());
        rutas.put("/preguntas/modificar", new PantallaModificarPregunta());
        rutas.put("/evaluar", new PantallaEvaluarCandidatos());
        rutas.put("/cuestionario", new PantallaCuestionario());
        rutas.put("/orden-merito", new PantallaOrdenMerito());
    }

    public void iniciar() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress(puerto), 0);
        hilos = Executors.newFixedThreadPool(16);
        servidor.setExecutor(hilos);
        servidor.createContext("/", this::atender);
        servidor.start();
    }

    public void detener() {
        if (servidor != null) {
            servidor.stop(0);
        }
        if (hilos != null) {
            hilos.shutdownNow();
        }
    }

    public int getPuerto() {
        return servidor == null ? puerto : servidor.getAddress().getPort();
    }

    private void atender(HttpExchange intercambio) throws IOException {
        RespuestaHttp respuesta;
        Peticion peticion = null;
        try {
            peticion = new Peticion(intercambio, sesiones);
            respuesta = despachar(peticion);
        } catch (ValidacionException e) {
            respuesta = RespuestaHttp.html(400, Html.pagina("Operación no válida",
                    peticion == null ? null : peticion.getSesion(),
                    Html.errores(e.getErrores()) + "<p><a class=\"boton\" href=\"/\">Volver</a></p>"));
        } catch (PersistenciaException e) {
            respuesta = RespuestaHttp.html(500, Html.pagina("Error de base de datos",
                    peticion == null ? null : peticion.getSesion(),
                    Html.errores(java.util.Collections.singletonList(e.getMessage()))
                    + "<p>Verifique que el servidor de base de datos esté funcionando y la configuración"
                    + " del archivo <code>config.properties</code>.</p>"
                    + "<p><a class=\"boton\" href=\"/\">Volver</a></p>"));
        } catch (RuntimeException e) {
            e.printStackTrace();
            respuesta = RespuestaHttp.html(500, Html.pagina("Error inesperado",
                    peticion == null ? null : peticion.getSesion(),
                    Html.errores(java.util.Collections.singletonList(
                            "Ocurrió un error inesperado: " + e.getClass().getSimpleName()
                            + (e.getMessage() == null ? "" : " - " + e.getMessage())))
                    + "<p><a class=\"boton\" href=\"/\">Volver</a></p>"));
        } finally {
            ConexionBD.cerrar();
        }
        enviar(intercambio, peticion, respuesta);
    }

    private RespuestaHttp despachar(Peticion peticion) {
        String ruta = peticion.getRuta();
        if (ruta.startsWith("/estaticos/")) {
            return recursoEstatico(ruta.substring("/estaticos/".length()));
        }
        if ("/".equals(ruta)) {
            Sesion sesion = peticion.getSesion();
            if (sesion != null && sesion.get(Sesion.CONSULTOR) != null) {
                return RespuestaHttp.redirigir("/principal");
            }
            if (sesion != null && sesion.get(Sesion.CANDIDATO) != null) {
                return RespuestaHttp.redirigir("/cuestionario");
            }
            return RespuestaHttp.redirigir("/login");
        }
        String elegida = null;
        for (String prefijo : rutas.keySet()) {
            boolean coincide = ruta.equals(prefijo) || ruta.startsWith(prefijo + "/");
            if (coincide && (elegida == null || prefijo.length() > elegida.length())) {
                elegida = prefijo;
            }
        }
        if (elegida == null) {
            return RespuestaHttp.html(404, Html.pagina("Página no encontrada", peticion.getSesion(),
                    "<p>La dirección solicitada no existe.</p><p><a class=\"boton\" href=\"/\">Ir al inicio</a></p>"));
        }
        return rutas.get(elegida).atender(peticion);
    }

    private RespuestaHttp recursoEstatico(String nombre) {
        if (!nombre.matches("[a-zA-Z0-9_-]+\\.(css|js|svg|png)")) {
            return RespuestaHttp.html(404, "No encontrado");
        }
        try (InputStream in = ServidorWeb.class.getResourceAsStream("/static/" + nombre)) {
            if (in == null) {
                return RespuestaHttp.html(404, "No encontrado");
            }
            String tipo = nombre.endsWith(".css") ? "text/css; charset=UTF-8"
                    : nombre.endsWith(".js") ? "application/javascript; charset=UTF-8"
                    : nombre.endsWith(".svg") ? "image/svg+xml" : "image/png";
            return RespuestaHttp.recurso(tipo, in.readAllBytes());
        } catch (IOException e) {
            return RespuestaHttp.html(500, "No se pudo leer el recurso");
        }
    }

    private static void enviar(HttpExchange intercambio, Peticion peticion, RespuestaHttp respuesta)
            throws IOException {
        try {
            intercambio.getResponseHeaders().set("Content-Type", respuesta.getTipo());
            for (Map.Entry<String, String> encabezado : respuesta.getEncabezados().entrySet()) {
                intercambio.getResponseHeaders().set(encabezado.getKey(), encabezado.getValue());
            }
            if (peticion != null) {
                if (peticion.getSesion() != null && peticion.isSesionNueva()) {
                    intercambio.getResponseHeaders().add("Set-Cookie", GestorSesiones.COOKIE + "="
                            + peticion.getSesion().getId() + "; Path=/; HttpOnly; SameSite=Lax");
                } else if (peticion.getSesion() == null && peticion.getIdSesionCookie() != null) {
                    intercambio.getResponseHeaders().add("Set-Cookie", GestorSesiones.COOKIE
                            + "=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax");
                }
            }
            intercambio.getResponseHeaders().set("X-Content-Type-Options", "nosniff");
            byte[] cuerpo = respuesta.getCuerpo();
            if (cuerpo.length == 0) {
                // el servidor del JDK cierra la conexión después de una respuesta sin cuerpo
                intercambio.getResponseHeaders().set("Connection", "close");
            }
            intercambio.sendResponseHeaders(respuesta.getEstado(), cuerpo.length == 0 ? -1 : cuerpo.length);
            if (cuerpo.length > 0) {
                try (OutputStream salida = intercambio.getResponseBody()) {
                    salida.write(cuerpo);
                }
            }
        } finally {
            intercambio.close();
        }
    }
}
