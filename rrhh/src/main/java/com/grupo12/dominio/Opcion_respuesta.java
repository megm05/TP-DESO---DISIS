package com.grupo12.dominio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Opción de respuesta: conjunto reutilizable de opciones que puede vincularse a
 * las preguntas (por ejemplo "VERDADERO - FALSO"). Tiene ABM propio (CU024, CU029-031).
 */
public class Opcion_respuesta {

    private int id_opcion;
    private String nombre;
    private String descripcion;
    private List<Detalle_opcion> detalles = new ArrayList<>();

    /** Lista de opciones ordenada por orden de visualización. */
    public List<Detalle_opcion> obtener_detalles() {
        List<Detalle_opcion> ordenados = new ArrayList<>(detalles);
        ordenados.sort(Comparator.comparingInt(Detalle_opcion::getOrden));
        return ordenados;
    }

    public int getId_opcion() {
        return id_opcion;
    }

    public void setId_opcion(int id_opcion) {
        this.id_opcion = id_opcion;
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

    public void setDetalles(List<Detalle_opcion> detalles) {
        this.detalles = detalles;
    }
}
