package com.grupo12.dominio;

/**
 * Estados por los que pasa un cuestionario.
 * <ul>
 *   <li>ACTIVO: generado al crear la evaluación (CU025), todavía no iniciado.</li>
 *   <li>EN_PROCESO: el candidato aceptó las instrucciones y está respondiendo (CU026).</li>
 *   <li>COMPLETO: respondió todos los bloques (CU026 paso 8).</li>
 *   <li>INCOMPLETO: venció el tiempo para completarlo o superó los accesos permitidos.</li>
 *   <li>SIN_CONTESTAR: venció el tiempo máximo en estado activo sin que ingresara.</li>
 * </ul>
 */
public enum Estado {
    ACTIVO,
    EN_PROCESO,
    COMPLETO,
    INCOMPLETO,
    SIN_CONTESTAR
}
