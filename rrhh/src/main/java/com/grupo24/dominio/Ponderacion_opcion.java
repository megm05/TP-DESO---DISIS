package com.grupo24.dominio;

/** Clase asociación entre Pregunta y Detalle_opcion: ponderación que la pregunta asigna a esa opción. */
public class Ponderacion_opcion {

    private double ponderacion;
    private Detalle_opcion detalle;

    public Ponderacion_opcion() {
    }

    public Ponderacion_opcion(Detalle_opcion detalle, double ponderacion) {
        this.detalle = detalle;
        this.ponderacion = ponderacion;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(double ponderacion) {
        this.ponderacion = ponderacion;
    }

    public Detalle_opcion getDetalle() {
        return detalle;
    }

    public void setDetalle(Detalle_opcion detalle) {
        this.detalle = detalle;
    }
}
