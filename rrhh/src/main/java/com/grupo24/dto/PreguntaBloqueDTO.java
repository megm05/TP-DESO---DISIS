package com.grupo24.dto;

import java.util.List;

/**
 * Pregunta de un bloque tal como se le presenta al candidato (CU026 paso 4): el enunciado y los textos de las
 * opciones, SIN las ponderaciones.
 */
public class PreguntaBloqueDTO {

    private final int id_respuesta;
    private final int numero;
    private final String enunciado;
    private final List<OpcionBloqueDTO> opciones;
    private final Integer id_detalle_elegido;

    public PreguntaBloqueDTO(
            int id_respuesta,
            int numero,
            String enunciado,
            List<OpcionBloqueDTO> opciones,
            Integer id_detalle_elegido) {
        this.id_respuesta = id_respuesta;
        this.numero = numero;
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.id_detalle_elegido = id_detalle_elegido;
    }

    public int getId_respuesta() {
        return id_respuesta;
    }

    public int getNumero() {
        return numero;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public List<OpcionBloqueDTO> getOpciones() {
        return opciones;
    }

    public Integer getId_detalle_elegido() {
        return id_detalle_elegido;
    }
}
