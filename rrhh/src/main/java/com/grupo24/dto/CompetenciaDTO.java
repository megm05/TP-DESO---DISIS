package com.grupo24.dto;

/** Competencia de la lista de selección del alta de preguntas (CU021). */
public class CompetenciaDTO {

    private final int id_competencia;
    private final String nombre;
    private final String tipo;

    public CompetenciaDTO(int id_competencia, String nombre, String tipo) {
        this.id_competencia = id_competencia;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getId_competencia() {
        return id_competencia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }
}
