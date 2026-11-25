package com.grupo12.persistencia;

import com.grupo12.dominio.Opcion_respuesta;
import java.util.List;

/** Acceso a los datos de las opciones de respuesta y su lista de opciones. */
public interface OpcionRespuestaDAO {

    Opcion_respuesta obtener_por_id(int id);

    List<Opcion_respuesta> obtener_todos();
}
