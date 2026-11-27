package com.grupo12.dominio;

import java.util.ArrayList;
import java.util.List;

/** Bloque de preguntas de un cuestionario (un paso del wizard). */
public class BloquePregunta {

    private int id_bloque;
    private int numero;
    private boolean completado;
    private List<Respuesta> respuestas = new ArrayList<>();

    public void completar() {
        this.completado = true;
    }

    /** Indica si el candidato respondió todas las preguntas del bloque (CU026 paso 6). */
    public boolean esta_completo() {
        for (Respuesta respuesta : respuestas) {
            if (respuesta.getDetalle_elegido() == null) {
                return false;
            }
        }
        return !respuestas.isEmpty();
    }

    public List<Respuesta> obtener_respuestas() {
        return respuestas;
    }

    public int getId_bloque() {
        return id_bloque;
    }

    public void setId_bloque(int id_bloque) {
        this.id_bloque = id_bloque;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public void setRespuestas(List<Respuesta> respuestas) {
        this.respuestas = respuestas;
    }
}
