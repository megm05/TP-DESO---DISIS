package com.grupo12.persistencia;

import com.grupo12.dominio.OrdenMerito;

/** Acceso a los órdenes de mérito emitidos. */
public interface OrdenMeritoDAO {

    /** Guarda el orden de mérito emitido, las evaluaciones que abarca y sus renglones. */
    void guardar(OrdenMerito ordenMerito);
}
