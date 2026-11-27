package com.grupo12.dominio;

import java.util.ArrayList;
import java.util.List;

/** Factor de una competencia. */
public class Factor {

    public static final int PREGUNTAS_POR_FACTOR = 2;

    private int id_factor;
    private String nombre;
    private String descripcion;
    private Competencia competencia;
    private List<Pregunta> preguntas = new ArrayList<>();

    /**
     * Un factor puede evaluarse si tiene al menos 2 preguntas vigentes (las dadas de baja lógicamente no
     * cuentan).
     */
    public boolean preguntas_suficientes() {
        int vigentes = 0;
        for (Pregunta pregunta : preguntas) {
            if (pregunta.disponibilidad()) {
                vigentes++;
            }
        }
        return vigentes >= PREGUNTAS_POR_FACTOR;
    }

    public int getId_factor() {
        return id_factor;
    }

    public void setId_factor(int id_factor) {
        this.id_factor = id_factor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public List<Pregunta> getPreguntas() {
        return preguntas;
    }

    public void setPreguntas(List<Pregunta> preguntas) {
        this.preguntas = preguntas;
    }
}
