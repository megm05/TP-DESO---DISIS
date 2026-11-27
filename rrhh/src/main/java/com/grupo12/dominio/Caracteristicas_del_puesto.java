package com.grupo12.dominio;

/**
 * Clase asociación entre Puesto y Competencia: ponderación mínima que debe obtener un candidato en la
 * competencia para ajustarse al puesto.
 */
public class Caracteristicas_del_puesto {

    private double ponderacion;
    private Competencia competencia;

    public Caracteristicas_del_puesto() {
    }

    public Caracteristicas_del_puesto(Competencia competencia, double ponderacion) {
        this.competencia = competencia;
        this.ponderacion = ponderacion;
    }

    public boolean cumple_ponderacion(double puntaje) {
        return puntaje >= ponderacion;
    }

    public double getPonderacion() {
        return ponderacion;
    }

    public void setPonderacion(double ponderacion) {
        this.ponderacion = ponderacion;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }
}
