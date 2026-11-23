package com.grupo12.dominio;

/**
 * Clase asociación entre {@link Pregunta} y {@link Detalle_opcion}: ponderación
 * que la pregunta asigna a esa opción. Las ponderaciones de una pregunta suman 10
 * y son siempre 0 o positivas (CU021, observaciones).
 */
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
