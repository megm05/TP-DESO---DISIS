package com.grupo12.dominio;

import java.util.ArrayList;
import java.util.List;

/** Puesto o función que una empresa solicita cubrir. */
public class Puesto {

    private int id_puesto;
    private String nombre;
    private String descripcion;
    private Empresa empresa;
    private List<Caracteristicas_del_puesto> caracteristicas = new ArrayList<>();

    public List<Competencia> obtener_competencias() {
        List<Competencia> competencias = new ArrayList<>();
        for (Caracteristicas_del_puesto caracteristica : caracteristicas) {
            competencias.add(caracteristica.getCompetencia());
        }
        return competencias;
    }

    /**
     * Indica si el puesto puede ser evaluado: todas sus competencias deben poseer
     * al menos un factor en condiciones de ser evaluado (CU025 paso 9).
     */
    public boolean evaluables() {
        for (Competencia competencia : obtener_competencias()) {
            if (!competencia.factores_evaluables()) {
                return false;
            }
        }
        return true;
    }

    public double obtener_ponderacion_minima(Competencia competencia) {
        for (Caracteristicas_del_puesto caracteristica : caracteristicas) {
            if (caracteristica.getCompetencia().getId_competencia() == competencia.getId_competencia()) {
                return caracteristica.getPonderacion();
            }
        }
        return 0;
    }

    public int getId_puesto() {
        return id_puesto;
    }

    public void setId_puesto(int id_puesto) {
        this.id_puesto = id_puesto;
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public List<Caracteristicas_del_puesto> getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(List<Caracteristicas_del_puesto> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }
}
