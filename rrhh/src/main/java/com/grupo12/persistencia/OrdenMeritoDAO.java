package com.grupo12.persistencia;

import com.grupo12.dominio.OrdenMerito;

/** Acceso a los órdenes de mérito emitidos. */
public interface OrdenMeritoDAO {

    void guardar(OrdenMerito ordenMerito);
}
