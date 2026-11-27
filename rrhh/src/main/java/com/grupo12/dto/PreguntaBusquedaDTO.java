package com.grupo12.dto;

/** Fila de la lista de preguntas (CU020 paso 4): combina competencia, factor y pregunta. */
public class PreguntaBusquedaDTO {

    private final int id_pregunta;
    private final String nombre_competencia;
    private final String nombre_factor;
    private final String nombre_pregunta;
    private final String pregunta;

    public PreguntaBusquedaDTO(
            int id_pregunta,
            String nombre_competencia,
            String nombre_factor,
            String nombre_pregunta,
            String pregunta) {
        this.id_pregunta = id_pregunta;
        this.nombre_competencia = nombre_competencia;
        this.nombre_factor = nombre_factor;
        this.nombre_pregunta = nombre_pregunta;
        this.pregunta = pregunta;
    }

    public int getId_pregunta() {
        return id_pregunta;
    }

    public String getNombre_competencia() {
        return nombre_competencia;
    }

    public String getNombre_factor() {
        return nombre_factor;
    }

    public String getNombre_pregunta() {
        return nombre_pregunta;
    }

    public String getPregunta() {
        return pregunta;
    }
}
