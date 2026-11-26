package com.grupo12.gestores;

import com.grupo12.dominio.Consultor;

/**
 * Contrato común de los gestores de ABM (preguntas, factores, competencias,
 * puestos y opciones de respuesta). La búsqueda no forma parte del contrato
 * porque cada caso de uso de gestión define criterios distintos.
 *
 * @param <D> datos que la pantalla envía para el alta y la modificación
 */
public interface Gestor<D> {

    void dar_de_alta(D datos);

    void modificar(D datos);

    /**
     * @param id        identificador del elemento a dar de baja
     * @param consultor usuario que realiza la baja (queda en el registro de auditoría)
     */
    void dar_baja(int id, Consultor consultor);
}
