package com.grupo24.persistencia;

import com.grupo24.dominio.Puesto;
import java.util.List;

/** Acceso a los datos de los puestos, con su empresa y sus características. */
public interface PuestoDAO {

    Puesto obtener_por_id(int id);

    List<Puesto> buscar_por_filtros(Integer codigo, String nombre, String empresa);
}
