package com.grupo24.dominio;

/**
 * Parámetro del repositorio de configuraciones del sistema (CU026, observaciones): instrucciones del
 * cuestionario, preguntas por bloque, tiempos y accesos máximos.
 */
public class Parametro {

    public static final String INSTRUCCIONES = "INSTRUCCIONES";
    public static final String PREGUNTAS_POR_BLOQUE = "PREGUNTAS_POR_BLOQUE";
    public static final String TIEMPO_MAX_COMPLETAR = "TIEMPO_MAX_COMPLETAR";
    public static final String TIEMPO_MAX_ACTIVO = "TIEMPO_MAX_ACTIVO";
    public static final String MAX_ACCESOS = "MAX_ACCESOS";

    private String clave;
    private String valor;
    private String descripcion;

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
