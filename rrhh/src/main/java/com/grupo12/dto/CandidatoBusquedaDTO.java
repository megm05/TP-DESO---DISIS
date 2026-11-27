package com.grupo12.dto;

/** Fila de la grilla de búsqueda de candidatos (CU025 paso 3). */
public class CandidatoBusquedaDTO {

    private final int num_candidato;
    private final String apellido;
    private final String nombre;
    private final String tipo_doc;
    private final String nro_doc;

    public CandidatoBusquedaDTO(
            int num_candidato,
            String apellido,
            String nombre,
            String tipo_doc,
            String nro_doc) {
        this.num_candidato = num_candidato;
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipo_doc = tipo_doc;
        this.nro_doc = nro_doc;
    }

    public int getNum_candidato() {
        return num_candidato;
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
}
