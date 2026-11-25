package com.grupo12.dominio;

/**
 * Renglón del orden de mérito: un candidato de las evaluaciones seleccionadas, con
 * su posición (si integra el orden de mérito) y su puntaje (si completó el cuestionario).
 */
public class Renglon_Orden_Merito {

    private int nro_renglon;
    /** Posición en el orden de mérito; null si el candidato no lo integra. */
    private Integer posicion;
    /** Puntaje obtenido; null si el cuestionario no está completo. */
    private Double puntaje_obtenido;
    private Candidato candidato;
    private Cuestionario cuestionario;

    public int getNro_renglon() {
        return nro_renglon;
    }

    public void setNro_renglon(int nro_renglon) {
        this.nro_renglon = nro_renglon;
    }

    public Integer getPosicion() {
        return posicion;
    }

    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }

    public Double getPuntaje_obtenido() {
        return puntaje_obtenido;
    }

    public void setPuntaje_obtenido(Double puntaje_obtenido) {
        this.puntaje_obtenido = puntaje_obtenido;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public Cuestionario getCuestionario() {
        return cuestionario;
    }

    public void setCuestionario(Cuestionario cuestionario) {
        this.cuestionario = cuestionario;
    }
}
