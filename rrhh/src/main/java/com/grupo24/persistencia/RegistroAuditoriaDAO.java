package com.grupo24.persistencia;

import com.grupo24.dominio.Auditoria;

/** Acceso a los registros de auditoría de las bajas. */
public interface RegistroAuditoriaDAO {

    void guardar(Auditoria registro);
}
