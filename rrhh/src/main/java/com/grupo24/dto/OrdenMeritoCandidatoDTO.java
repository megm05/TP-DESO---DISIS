package com.grupo24.dto;

import java.time.LocalDateTime;

/** Renglón del reporte de orden de mérito (CU028 paso 7). */
public class OrdenMeritoCandidatoDTO {

    private final Integer posicion;
    private final String tipo_doc;
    private final String nro_doc;
    private final String apellido;
    private final String nombre;
    private final Double puntaje_obtenido;
    private final String estado_cuestionario;
    private final LocalDateTime fecha_inicio;
    private final LocalDateTime fecha_fin;
    private final LocalDateTime ultimo_ingreso;
    private final int cant_accesos;

    public OrdenMeritoCandidatoDTO(
            Integer posicion,
            String tipo_doc,
            String nro_doc,
            String apellido,
            String nombre,
            Double puntaje_obtenido,
            String estado_cuestionario,
            LocalDateTime fecha_inicio,
            LocalDateTime fecha_fin,
            LocalDateTime ultimo_ingreso,
            int cant_accesos) {
        this.posicion = posicion;
        this.tipo_doc = tipo_doc;
        this.nro_doc = nro_doc;
        this.apellido = apellido;
        this.nombre = nombre;
        this.puntaje_obtenido = puntaje_obtenido;
        this.estado_cuestionario = estado_cuestionario;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.ultimo_ingreso = ultimo_ingreso;
        this.cant_accesos = cant_accesos;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public String getTipo_doc() {
        return tipo_doc;
    }

    public String getNro_doc() {
        return nro_doc;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPuntaje_obtenido() {
        return puntaje_obtenido;
    }

    public String getEstado_cuestionario() {
        return estado_cuestionario;
    }

    public LocalDateTime getFecha_inicio() {
        return fecha_inicio;
    }

    public LocalDateTime getFecha_fin() {
        return fecha_fin;
    }

    public LocalDateTime getUltimo_ingreso() {
        return ultimo_ingreso;
    }

    public int getCant_accesos() {
        return cant_accesos;
    }
}
