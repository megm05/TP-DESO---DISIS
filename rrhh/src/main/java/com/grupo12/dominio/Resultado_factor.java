package com.grupo12.dominio;

/**
 * Total obtenido por un candidato en un factor: suma de las ponderaciones de las
 * opciones elegidas en las preguntas del factor, dividido 2 (CU026 observación 3).
 */
public class Resultado_factor {

    private double total;
    private Factor factor;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Factor getFactor() {
        return factor;
    }

    public void setFactor(Factor factor) {
        this.factor = factor;
    }
}
