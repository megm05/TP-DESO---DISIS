package com.grupo24.dto;

/** Factor de la competencia seleccionada en el alta de preguntas (CU021). */
public class FactorDTO {

    private final int id_factor;
    private final String nombre;

    public FactorDTO(int id_factor, String nombre) {
        this.id_factor = id_factor;
        this.nombre = nombre;
    }

    public int getId_factor() {
        return id_factor;
    }

    public String getNombre() {
        return nombre;
    }
}
