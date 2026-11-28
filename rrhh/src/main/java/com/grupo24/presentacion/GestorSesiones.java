package com.grupo24.presentacion;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Crea, recupera y vence las sesiones, identificadas por una cookie. */
final class GestorSesiones {

    static final String COOKIE = "SESION_RRHH";
    /** Una sesión sin actividad durante 60 minutos se descarta. */
    private static final long VIGENCIA_MS = 60L * 60 * 1000;

    private final Map<String, Sesion> sesiones = new ConcurrentHashMap<>();
    private final SecureRandom azar = new SecureRandom();

    Sesion obtener(String id) {
        vencer();
        Sesion sesion = id == null ? null : sesiones.get(id);
        if (sesion != null) {
            sesion.tocar();
        }
        return sesion;
    }

    Sesion crear() {
        byte[] bytes = new byte[24];
        azar.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        Sesion sesion = new Sesion(sb.toString());
        sesiones.put(sesion.getId(), sesion);
        return sesion;
    }

    void eliminar(Sesion sesion) {
        if (sesion != null) {
            sesion.invalidar();
            sesiones.remove(sesion.getId());
        }
    }

    private void vencer() {
        long limite = System.currentTimeMillis() - VIGENCIA_MS;
        for (Iterator<Sesion> it = sesiones.values().iterator(); it.hasNext();) {
            if (it.next().getUltimoAcceso() < limite) {
                it.remove();
            }
        }
    }
}
