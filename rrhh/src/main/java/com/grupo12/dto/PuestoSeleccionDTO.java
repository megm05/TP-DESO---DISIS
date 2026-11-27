package com.grupo12.dto;

/** Función o puesto con el nombre de su empresa, para seleccionarlo (CU025 paso 8). */
public class PuestoSeleccionDTO {

    private final int id_puesto;
    private final String nombre_puesto;
    private final String nombre_empresa;

    public PuestoSeleccionDTO(int id_puesto, String nombre_puesto, String nombre_empresa) {
        this.id_puesto = id_puesto;
        this.nombre_puesto = nombre_puesto;
        this.nombre_empresa = nombre_empresa;
    }

    public int getId_puesto() {
        return id_puesto;
    }

    public String getNombre_puesto() {
        return nombre_puesto;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }
}
