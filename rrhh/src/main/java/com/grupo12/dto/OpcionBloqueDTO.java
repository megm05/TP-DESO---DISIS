package com.grupo12.dto;

/** Opción que puede elegir el candidato para una pregunta. */
public class OpcionBloqueDTO {

    private final int id_detalle;
    private final String descripcion;

    public OpcionBloqueDTO(int id_detalle, String descripcion) {
        this.id_detalle = id_detalle;
        this.descripcion = descripcion;
    }

    public int getId_detalle() {
        return id_detalle;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
