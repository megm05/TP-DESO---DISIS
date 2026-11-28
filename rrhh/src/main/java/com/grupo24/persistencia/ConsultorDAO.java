package com.grupo24.persistencia;

import com.grupo24.dominio.Consultor;

/** Acceso a los datos de los consultores. */
public interface ConsultorDAO {

    Consultor obtener_por_id(int id);

    Consultor obtener_por_nombre_usuario(String nombre_usuario);
}
