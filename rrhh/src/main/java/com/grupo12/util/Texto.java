package com.grupo12.util;

import java.util.Locale;

/** Utilidades para el tratamiento de los textos que ingresa el actor. */
public final class Texto {

    private Texto() {
    }

    /**
     * Normaliza un dato literal: sin espacios sobrantes y SIEMPRE en mayúsculas
     * (requerimiento especial de los casos de uso). Devuelve "" si es nulo.
     */
    public static String mayusculas(String texto) {
        return texto == null ? "" : texto.trim().toUpperCase(Locale.ROOT);
    }

    public static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
