package com.grupo12.presentacion;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Consultor;

/**
 * Clase base de las pantallas (clases de interfaz). Cada pantalla atiende los
 * pedidos de su caso de uso y colabora con los gestores correspondientes.
 */
public abstract class Pantalla {

    /** Atiende un pedido dirigido a esta pantalla. */
    public abstract RespuestaHttp atender(Peticion peticion);

    /** Consultor autenticado en la sesión, o null. */
    protected static Consultor consultor(Peticion peticion) {
        Sesion sesion = peticion.getSesion();
        return sesion == null ? null : sesion.get(Sesion.CONSULTOR);
    }

    /** Candidato autenticado en la sesión, o null. */
    protected static Candidato candidato(Peticion peticion) {
        Sesion sesion = peticion.getSesion();
        return sesion == null ? null : sesion.get(Sesion.CANDIDATO);
    }

    protected static RespuestaHttp pagina(Peticion peticion, String titulo, String contenido) {
        return RespuestaHttp.html(Html.pagina(titulo, peticion.getSesion(), contenido));
    }

    /** Las pantallas del consultor requieren un consultor autenticado (CU001). */
    protected static boolean esConsultor(Peticion peticion) {
        return consultor(peticion) != null;
    }
}
