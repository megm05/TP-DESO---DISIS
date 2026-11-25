package com.grupo12.persistencia;

import com.grupo12.dominio.Puesto;
import java.util.List;

/** Acceso a los datos de los puestos, con su empresa y sus características. */
public interface PuestoDAO {

    Puesto obtener_por_id(int id);

    /**
     * Búsqueda de puestos por código, nombre del puesto y empresa. Nombre y empresa
     * se buscan por "contiene"; los criterios vacíos o nulos no se aplican.
     */
    List<Puesto> buscar_por_filtros(Integer codigo, String nombre, String empresa);
}
