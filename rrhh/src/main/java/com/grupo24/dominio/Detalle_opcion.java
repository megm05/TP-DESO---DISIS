package com.grupo24.dominio;

/** Cada una de las opciones (orden de visualización y descripción) de una opción de respuesta. */
public class Detalle_opcion {

    private int id_detalle;
    private int orden;
    private String descripcion;

    public Detalle_opcion() {
    }

    public Detalle_opcion(int id_detalle, int orden, String descripcion) {
        this.id_detalle = id_detalle;
        this.orden = orden;
        this.descripcion = descripcion;
    }

    public int getId_detalle() {
        return id_detalle;
    }

    public void setId_detalle(int id_detalle) {
        this.id_detalle = id_detalle;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
