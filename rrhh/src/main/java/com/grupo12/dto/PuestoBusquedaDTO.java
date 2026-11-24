package com.grupo12.dto;

/**
 * Fila de la lista de puestos del orden de mérito (CU028 paso 4). Incluye valores
 * calculados: cantidad de candidatos y de evaluaciones completas.
 */
public class PuestoBusquedaDTO {

    private final int codigo;
    private final String nombre_puesto;
    private final String nombre_empresa;
    private final int cant_candidatos;
    private final int cant_evaluaciones_completas;

    public PuestoBusquedaDTO(
            int codigo,
            String nombre_puesto,
            String nombre_empresa,
            int cant_candidatos,
            int cant_evaluaciones_completas) {
        this.codigo = codigo;
        this.nombre_puesto = nombre_puesto;
        this.nombre_empresa = nombre_empresa;
        this.cant_candidatos = cant_candidatos;
        this.cant_evaluaciones_completas = cant_evaluaciones_completas;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getNombre_puesto() {
        return nombre_puesto;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public int getCant_candidatos() {
        return cant_candidatos;
    }

    public int getCant_evaluaciones_completas() {
        return cant_evaluaciones_completas;
    }
}
