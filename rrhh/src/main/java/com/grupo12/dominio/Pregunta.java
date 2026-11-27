package com.grupo12.dominio;

import java.util.ArrayList;
import java.util.List;

/** Pregunta cerrada de elección única asociada a un factor. */
public class Pregunta {

    private int id_pregunta;
    private String nombre;
    private String pregunta;
    private String descripcion;
    private boolean activo = true;
    private Factor factor;
    private Opcion_respuesta opcion_respuesta;
    private List<Ponderacion_opcion> ponderaciones = new ArrayList<>();

    public boolean disponibilidad() {
        return activo;
    }

    /** Baja lógica de la pregunta (CU023 paso 3). */
    public void desactivar() {
        this.activo = false;
    }

    public double obtener_ponderacion(Detalle_opcion detalle) {
        if (detalle == null) {
            return 0;
        }
        for (Ponderacion_opcion ponderacion : ponderaciones) {
            if (ponderacion.getDetalle().getId_detalle() == detalle.getId_detalle()) {
                return ponderacion.getPonderacion();
            }
        }
        return 0;
    }

    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Factor getFactor() {
        return factor;
    }

    public void setFactor(Factor factor) {
        this.factor = factor;
    }

    public Opcion_respuesta getOpcion_respuesta() {
        return opcion_respuesta;
    }

    public void setOpcion_respuesta(Opcion_respuesta opcion_respuesta) {
        this.opcion_respuesta = opcion_respuesta;
    }

    public List<Ponderacion_opcion> getPonderaciones() {
        return ponderaciones;
    }

    public void setPonderaciones(List<Ponderacion_opcion> ponderaciones) {
        this.ponderaciones = ponderaciones;
    }
}
