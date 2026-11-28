package com.grupo24.presentacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Sesión de trabajo de un usuario en el navegador. */
public class Sesion {

    public static final String CONSULTOR = "consultor";
    public static final String CANDIDATO = "candidato";

    private final String id;
    private final Map<String, Object> atributos = new ConcurrentHashMap<>();
    private final List<String> mensajes = new ArrayList<>();
    private volatile long ultimoAcceso = System.currentTimeMillis();

    Sesion(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String clave) {
        return (T) atributos.get(clave);
    }

    public void put(String clave, Object valor) {
        if (valor == null) {
            atributos.remove(clave);
        } else {
            atributos.put(clave, valor);
        }
    }

    public void quitar(String clave) {
        atributos.remove(clave);
    }

    public synchronized void agregarMensaje(String mensaje) {
        mensajes.add(mensaje);
    }

    public synchronized List<String> tomarMensajes() {
        List<String> pendientes = new ArrayList<>(mensajes);
        mensajes.clear();
        return pendientes;
    }

    void tocar() {
        ultimoAcceso = System.currentTimeMillis();
    }

    long getUltimoAcceso() {
        return ultimoAcceso;
    }

    void invalidar() {
        atributos.clear();
    }
}
