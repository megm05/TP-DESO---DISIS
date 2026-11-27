package com.grupo12.presentacion;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pedido HTTP recibido desde el navegador, con sus parámetros ya decodificados. */
public class Peticion {

    private final String metodo;
    private final String ruta;
    private final Map<String, List<String>> parametros = new LinkedHashMap<>();
    private final GestorSesiones gestorSesiones;
    private final String idSesionCookie;
    private Sesion sesion;
    private boolean sesionNueva;

    Peticion(HttpExchange intercambio, GestorSesiones gestorSesiones) throws IOException {
        this.metodo = intercambio.getRequestMethod().toUpperCase();
        this.ruta = intercambio.getRequestURI().getPath();
        this.gestorSesiones = gestorSesiones;
        decodificar(intercambio.getRequestURI().getRawQuery());
        if ("POST".equals(metodo)) {
            try (InputStream cuerpo = intercambio.getRequestBody()) {
                decodificar(new String(cuerpo.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        this.idSesionCookie = leerCookie(intercambio.getRequestHeaders().getFirst("Cookie"));
        this.sesion = gestorSesiones.obtener(idSesionCookie);
    }

    private void decodificar(String texto) throws UnsupportedEncodingException {
        if (texto == null || texto.isEmpty()) {
            return;
        }
        for (String par : texto.split("&")) {
            if (par.isEmpty()) {
                continue;
            }
            int igual = par.indexOf('=');
            String clave = URLDecoder.decode(igual < 0 ? par : par.substring(0, igual), "UTF-8");
            String valor = igual < 0 ? "" : URLDecoder.decode(par.substring(igual + 1), "UTF-8");
            parametros.computeIfAbsent(clave, k -> new ArrayList<>()).add(valor);
        }
    }

    private static String leerCookie(String encabezado) {
        if (encabezado == null) {
            return null;
        }
        for (String cookie : encabezado.split(";")) {
            String[] partes = cookie.trim().split("=", 2);
            if (partes.length == 2 && GestorSesiones.COOKIE.equals(partes[0])) {
                return partes[1];
            }
        }
        return null;
    }

    public String getMetodo() {
        return metodo;
    }

    public boolean esPost() {
        return "POST".equals(metodo);
    }

    public String getRuta() {
        return ruta;
    }

    /** Valor del parámetro (sin espacios sobrantes), o "" si no vino. */
    public String param(String nombre) {
        List<String> valores = parametros.get(nombre);
        return valores == null || valores.isEmpty() ? "" : valores.get(0).trim();
    }

    /** Todos los valores de un parámetro que puede repetirse (por ejemplo, casillas de selección). */
    public List<String> params(String nombre) {
        List<String> valores = parametros.get(nombre);
        return valores == null ? Collections.emptyList() : valores;
    }

    /** Valor entero del parámetro, o null si no vino o no es un número. */
    public Integer paramEntero(String nombre) {
        try {
            String valor = param(nombre);
            return valor.isEmpty() ? null : Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Valor entero del parámetro, o 0 si no vino o no es un número. */
    public int paramEnteroOCero(String nombre) {
        Integer valor = paramEntero(nombre);
        return valor == null ? 0 : valor;
    }

    /** Valores enteros de un parámetro repetido (se descartan los que no son números). */
    public List<Integer> paramsEnteros(String nombre) {
        List<Integer> enteros = new ArrayList<>();
        for (String valor : params(nombre)) {
            try {
                enteros.add(Integer.valueOf(valor.trim()));
            } catch (NumberFormatException e) {
                // se ignora
            }
        }
        return enteros;
    }

    /** Nombres de los parámetros recibidos que empiezan con el prefijo indicado. */
    public List<String> nombresConPrefijo(String prefijo) {
        List<String> nombres = new ArrayList<>();
        for (String nombre : parametros.keySet()) {
            if (nombre.startsWith(prefijo)) {
                nombres.add(nombre);
            }
        }
        return nombres;
    }

    /** Sesión actual, o null si el navegador todavía no tiene una. */
    public Sesion getSesion() {
        return sesion;
    }

    /** Sesión actual; si no existe crea una nueva. */
    public Sesion obtenerOCrearSesion() {
        if (sesion == null) {
            sesion = gestorSesiones.crear();
            sesionNueva = true;
        }
        return sesion;
    }

    /** Descarta la sesión actual (cerrar sesión). */
    public void cerrarSesion() {
        gestorSesiones.eliminar(sesion);
        sesion = null;
    }

    boolean isSesionNueva() {
        return sesionNueva;
    }

    String getIdSesionCookie() {
        return idSesionCookie;
    }
}
