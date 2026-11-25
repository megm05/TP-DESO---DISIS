package com.grupo12.dominio;

import java.time.LocalDateTime;

/**
 * Registro de auditoría de la baja de una pregunta (CU023, requerimientos
 * especiales): usuario que la eliminó y fecha y hora de la eliminación.
 */
public class Registro_pregunta implements Auditoria {

    private int id_registro;
    private String nombre_borrado;
    private LocalDateTime fecha_hora;
    private Consultor consultor;

    /** Elimina lógicamente la pregunta y deja constancia de quién y cuándo lo hizo. */
    @Override
    public void Borrar_Con_Registro(Object instancia, LocalDateTime FechaYHora, Consultor usuario) {
        if (!(instancia instanceof Pregunta)) {
            throw new IllegalArgumentException("Registro_pregunta solo audita la baja de preguntas");
        }
        Pregunta pregunta = (Pregunta) instancia;
        pregunta.desactivar();
        this.nombre_borrado = pregunta.getNombre();
        this.fecha_hora = FechaYHora;
        this.consultor = usuario;
    }

    public int getId_registro() {
        return id_registro;
    }

    public void setId_registro(int id_registro) {
        this.id_registro = id_registro;
    }

    public String getNombre_borrado() {
        return nombre_borrado;
    }

    public LocalDateTime getFecha_hora() {
        return fecha_hora;
    }

    public Consultor getConsultor() {
        return consultor;
    }
}
