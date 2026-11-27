package com.grupo12.dto;

/** Archivo generado por el sistema para que el actor lo guarde (CU025 pasos 15 a 17). */
public class ArchivoDTO {

    private final String nombre_archivo;
    private final byte[] contenido;

    public ArchivoDTO(String nombre_archivo, byte[] contenido) {
        this.nombre_archivo = nombre_archivo;
        this.contenido = contenido;
    }

    public String getNombre_archivo() {
        return nombre_archivo;
    }

    public byte[] getContenido() {
        return contenido;
    }
}
