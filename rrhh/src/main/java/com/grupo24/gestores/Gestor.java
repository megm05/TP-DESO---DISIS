package com.grupo24.gestores;

import com.grupo24.dominio.Consultor;

/**
 * Contrato común de los gestores de ABM (preguntas, factores, competencias, puestos y opciones de respuesta).
 */
public interface Gestor<D> {

    void dar_de_alta(D datos);

    void modificar(D datos);

    void dar_baja(int id, Consultor consultor);
}
