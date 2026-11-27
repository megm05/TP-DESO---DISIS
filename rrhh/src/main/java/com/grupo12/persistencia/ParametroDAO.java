package com.grupo12.persistencia;

import com.grupo12.dominio.Parametro;

/** Acceso al repositorio de configuraciones del sistema. */
public interface ParametroDAO {

    Parametro obtener_por_clave(String clave);
}
