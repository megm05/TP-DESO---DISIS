package com.grupo24.dto;

/** Fila de la lista de candidatos a evaluar con su clave de ingreso (CU025 paso 11). */
public class CandidatoConClaveDTO {

    private final String apellido;
    private final String nombre;
    private final String tipo_doc;
    private final String nro_doc;
    private final String clave_ingreso;

    public CandidatoConClaveDTO(
            String apellido,
            String nombre,
            String tipo_doc,
            String nro_doc,
            String clave_ingreso) {
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipo_doc = tipo_doc;
        this.nro_doc = nro_doc;
        this.clave_ingreso = clave_ingreso;
    }

    public String getApellido() {
        return apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo_doc() {
        return tipo_doc;
    }

    public String getNro_doc() {
        return nro_doc;
    }

    public String getClave_ingreso() {
        return clave_ingreso;
    }
}
