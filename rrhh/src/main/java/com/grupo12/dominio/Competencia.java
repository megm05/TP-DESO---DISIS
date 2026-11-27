package com.grupo12.dominio;

import java.util.ArrayList;
import java.util.List;

/** Competencia técnica o actitudinal, compuesta por factores. */
public class Competencia {

    private int id_competencia;
    private String nombre;
    private String tipo;
    private List<Factor> factores = new ArrayList<>();

    /**
     * Una competencia puede evaluarse si posee al menos un factor con la cantidad mínima de preguntas (CU025
     * flujo 9.A).
     */
    public boolean factores_evaluables() {
        for (Factor factor : factores) {
            if (factor.preguntas_suficientes()) {
                return true;
            }
        }
        return false;
    }

    public List<Factor> obtener_factores() {
        return factores;
    }

    public int getId_competencia() {
        return id_competencia;
    }

    public void setId_competencia(int id_competencia) {
        this.id_competencia = id_competencia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setFactores(List<Factor> factores) {
        this.factores = factores;
    }
}
