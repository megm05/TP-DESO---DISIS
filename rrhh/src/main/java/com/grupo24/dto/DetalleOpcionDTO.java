package com.grupo24.dto;

/** Fila de la grilla Orden / Descripción / Ponderación del alta de preguntas (CU021). */
public class DetalleOpcionDTO {

    private final int id_detalle;
    private final int orden;
    private final String descripcion;

    public DetalleOpcionDTO(int id_detalle, int orden, String descripcion) {
        this.id_detalle = id_detalle;
        this.orden = orden;
        this.descripcion = descripcion;
    }

    public int getId_detalle() {
        return id_detalle;
    }

    public int getOrden() {
        return orden;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
