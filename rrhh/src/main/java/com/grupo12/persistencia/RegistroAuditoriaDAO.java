package com.grupo12.persistencia;

import com.grupo12.dominio.Auditoria;

/** Acceso a los registros de auditoría de las bajas. */
public interface RegistroAuditoriaDAO {

    void guardar(Auditoria registro);
}
