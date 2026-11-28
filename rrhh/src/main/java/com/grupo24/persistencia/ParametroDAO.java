package com.grupo24.persistencia;

import com.grupo24.dominio.Parametro;

/** Acceso al repositorio de configuraciones del sistema. */
public interface ParametroDAO {

    Parametro obtener_por_clave(String clave);
}
