package com.grupo24.dto;

/** Opción de respuesta de la lista de selección del alta de preguntas (CU021). */
public class OpcionRespuestaDTO {

    private final int id_opcion;
    private final String nombre;

    public OpcionRespuestaDTO(int id_opcion, String nombre) {
        this.id_opcion = id_opcion;
        this.nombre = nombre;
    }

    public int getId_opcion() {
        return id_opcion;
    }

    public String getNombre() {
        return nombre;
    }
}
