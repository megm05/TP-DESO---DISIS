package com.grupo12.dominio;

import java.time.LocalDateTime;

/**
 * Contrato de los registros de auditoría: toda baja debe dejar constancia del usuario que la realizó y de la
 * fecha y hora de la eliminación.
 */
public interface Auditoria {

    void Borrar_Con_Registro(Object instancia, LocalDateTime FechaYHora, Consultor usuario);
}
