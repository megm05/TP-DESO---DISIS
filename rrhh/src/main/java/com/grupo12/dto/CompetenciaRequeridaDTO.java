package com.grupo12.dto;

/** Competencia asociada a la función con la ponderación requerida (CU025 paso 9). */
public class CompetenciaRequeridaDTO {

    private final String nombre_competencia;
    private final double ponderacion_requerida;

    public CompetenciaRequeridaDTO(String nombre_competencia, double ponderacion_requerida) {
        this.nombre_competencia = nombre_competencia;
        this.ponderacion_requerida = ponderacion_requerida;
    }

    public String getNombre_competencia() {
        return nombre_competencia;
    }

    public double getPonderacion_requerida() {
        return ponderacion_requerida;
    }
}
