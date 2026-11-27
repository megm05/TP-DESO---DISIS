package com.grupo12.dominio;

import java.time.LocalDateTime;

/** Asocia una pregunta a un bloque del cuestionario y registra la opción que eligió el candidato. */
public class Respuesta {

    private int id_respuesta;
    private int orden;
    private LocalDateTime fecha;
    private Pregunta pregunta;
    private Detalle_opcion detalle_elegido;
    private Candidato candidato;

    public void registrar(Detalle_opcion detalle) {
        boolean pertenece = false;
        for (Detalle_opcion posible : pregunta.getOpcion_respuesta().obtener_detalles()) {
            if (posible.getId_detalle() == detalle.getId_detalle()) {
                pertenece = true;
                detalle = posible;
                break;
            }
        }
        if (!pertenece) {
            throw new IllegalArgumentException("La opción elegida no corresponde a la pregunta");
        }
        this.detalle_elegido = detalle;
        this.fecha = LocalDateTime.now();
    }

    public double obtener_ponderacion() {
        return pregunta.obtener_ponderacion(detalle_elegido);
    }

    public int getId_respuesta() {
        return id_respuesta;
    }

    public void setId_respuesta(int id_respuesta) {
        this.id_respuesta = id_respuesta;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

    public void setPregunta(Pregunta pregunta) {
        this.pregunta = pregunta;
    }

    public Detalle_opcion getDetalle_elegido() {
        return detalle_elegido;
    }

    public void setDetalle_elegido(Detalle_opcion detalle_elegido) {
        this.detalle_elegido = detalle_elegido;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }
}
