package com.grupo12.presentacion;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Respuesta que el servidor devuelve al navegador. */
public final class RespuestaHttp {

    private final int estado;
    private final String tipo;
    private final byte[] cuerpo;
    private final Map<String, String> encabezados = new LinkedHashMap<>();

    private RespuestaHttp(int estado, String tipo, byte[] cuerpo) {
        this.estado = estado;
        this.tipo = tipo;
        this.cuerpo = cuerpo;
    }

    public static RespuestaHttp html(String html) {
        return new RespuestaHttp(200, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
    }

    public static RespuestaHttp html(int estado, String html) {
        return new RespuestaHttp(estado, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8));
    }

    public static RespuestaHttp recurso(String tipo, byte[] contenido) {
        RespuestaHttp respuesta = new RespuestaHttp(200, tipo, contenido);
        respuesta.encabezados.put("Cache-Control", "no-cache");
        return respuesta;
    }

    /** Redirige el navegador a otra ruta (patrón POST / redirección / GET). */
    public static RespuestaHttp redirigir(String ruta) {
        // Se envía un cuerpo breve: con respuestas vacías el servidor del JDK cierra la
        // conexión sin avisarlo y el navegador puede intentar reutilizarla.
        String cuerpo = "<!DOCTYPE html><html lang=\"es\"><body><a href=\"" + Html.e(ruta)
                + "\">Continuar</a></body></html>";
        RespuestaHttp respuesta = new RespuestaHttp(303, "text/html; charset=UTF-8",
                cuerpo.getBytes(StandardCharsets.UTF_8));
        respuesta.encabezados.put("Location", ruta);
        return respuesta;
    }

    /** Archivo para descargar; el navegador permite elegir la carpeta donde guardarlo. */
    public static RespuestaHttp descarga(String nombreArchivo, String tipo, byte[] contenido) {
        RespuestaHttp respuesta = new RespuestaHttp(200, tipo, contenido);
        String ascii = nombreArchivo.replaceAll("[^A-Za-z0-9._ -]", "_");
        String codificado;
        try {
            codificado = URLEncoder.encode(nombreArchivo, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            codificado = ascii;
        }
        respuesta.encabezados.put("Content-Disposition",
                "attachment; filename=\"" + ascii + "\"; filename*=UTF-8''" + codificado);
        return respuesta;
    }

    int getEstado() {
        return estado;
    }

    String getTipo() {
        return tipo;
    }

    byte[] getCuerpo() {
        return cuerpo;
    }

    Map<String, String> getEncabezados() {
        return encabezados;
    }
}
