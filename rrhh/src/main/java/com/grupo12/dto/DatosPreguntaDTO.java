package com.grupo12.dto;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Datos de la pantalla de alta y de modificación de preguntas (CU021 y CU022): competencia, factor, nombre,
 * pregunta, descripción, opción de respuesta y la ponderación ingresada para cada opción.
 */
public class DatosPreguntaDTO {

    private int id_pregunta;
    private int id_competencia;
    private int id_factor;
    private String nombre_competencia;
    private String nombre_factor;
    private String nombre;
    private String pregunta;
    private String descripcion;
    private int id_opcion;
    private Map<Integer, Double> ponderaciones = new LinkedHashMap<>();

    public int getId_pregunta() {
        return id_pregunta;
    }

    public void setId_pregunta(int id_pregunta) {
        this.id_pregunta = id_pregunta;
    }

    public int getId_competencia() {
        return id_competencia;
    }

    public void setId_competencia(int id_competencia) {
        this.id_competencia = id_competencia;
    }

    public int getId_factor() {
        return id_factor;
    }

    public void setId_factor(int id_factor) {
        this.id_factor = id_factor;
    }

    public String getNombre_competencia() {
        return nombre_competencia;
    }

    public void setNombre_competencia(String nombre_competencia) {
        this.nombre_competencia = nombre_competencia;
    }

    public String getNombre_factor() {
        return nombre_factor;
    }

    public void setNombre_factor(String nombre_factor) {
        this.nombre_factor = nombre_factor;
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

    public int getId_opcion() {
        return id_opcion;
    }

    public void setId_opcion(int id_opcion) {
        this.id_opcion = id_opcion;
    }

    public Map<Integer, Double> getPonderaciones() {
        return ponderaciones;
    }

    public void setPonderaciones(Map<Integer, Double> ponderaciones) {
        this.ponderaciones = ponderaciones;
    }
}
