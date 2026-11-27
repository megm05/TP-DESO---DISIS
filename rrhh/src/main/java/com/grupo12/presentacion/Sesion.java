package com.grupo12.presentacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sesión de trabajo de un usuario en el navegador. Guarda el usuario autenticado
 * y el estado de los casos de uso que se desarrollan en varias pantallas.
 */
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

    /** Deja un mensaje para mostrar en la próxima pantalla (patrón POST / redirección / GET). */
    public synchronized void agregarMensaje(String mensaje) {
        mensajes.add(mensaje);
    }

    /** Devuelve y descarta los mensajes pendientes. */
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
