package com.grupo12.dominio;

import java.time.LocalDateTime;

/**
 * Contrato de los registros de auditoría: toda baja debe dejar constancia del
 * usuario que la realizó y de la fecha y hora de la eliminación.
 */
public interface Auditoria {

    /**
     * Da de baja la instancia y completa el registro de auditoría.
     *
     * @param instancia  elemento que se elimina
     * @param FechaYHora fecha y hora de la eliminación
     * @param usuario    consultor que realiza la baja
     */
    void Borrar_Con_Registro(Object instancia, LocalDateTime FechaYHora, Consultor usuario);
}
