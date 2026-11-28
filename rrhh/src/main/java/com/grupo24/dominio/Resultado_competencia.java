package com.grupo24.dominio;

import java.util.ArrayList;
import java.util.List;

/**
 * Total obtenido por un candidato en una competencia: suma de los totales de sus factores dividido la
 * cantidad de factores evaluados (CU026 observación 3).
 */
public class Resultado_competencia {

    private double total;
    private Competencia competencia;
    private List<Resultado_factor> resultados_factor = new ArrayList<>();

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Competencia getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Competencia competencia) {
        this.competencia = competencia;
    }

    public List<Resultado_factor> getResultados_factor() {
        return resultados_factor;
    }

    public void setResultados_factor(List<Resultado_factor> resultados_factor) {
        this.resultados_factor = resultados_factor;
    }
}
