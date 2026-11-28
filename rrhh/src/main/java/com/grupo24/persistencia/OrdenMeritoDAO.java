package com.grupo24.persistencia;

import com.grupo24.dominio.OrdenMerito;

/** Acceso a los órdenes de mérito emitidos. */
public interface OrdenMeritoDAO {

    void guardar(OrdenMerito ordenMerito);
}
