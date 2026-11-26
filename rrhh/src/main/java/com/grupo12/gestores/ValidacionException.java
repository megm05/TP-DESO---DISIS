package com.grupo12.gestores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Señala que los datos ingresados por el actor no superan las validaciones de un
 * caso de uso. Lleva TODOS los mensajes a mostrar (los flujos alternativos piden
 * detallar todas las omisiones en las que se ha incurrido).
 */
public class ValidacionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final List<String> errores;
    private final String campo;

    public ValidacionException(String error) {
        this(Collections.singletonList(error), null);
    }

    public ValidacionException(List<String> errores) {
        this(errores, null);
    }

    /**
     * @param errores mensajes a mostrar
     * @param campo   campo que debe recibir el foco al volver a la pantalla (puede ser null)
     */
    public ValidacionException(List<String> errores, String campo) {
        super(String.join(" ", errores));
        this.errores = new ArrayList<>(errores);
        this.campo = campo;
    }

    public List<String> getErrores() {
        return errores;
    }

    public String getCampo() {
        return campo;
    }
}
