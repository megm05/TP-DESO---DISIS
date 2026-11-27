package com.grupo12.presentacion;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Consultor;

/** Clase base de las pantallas (clases de interfaz). */
public abstract class Pantalla {

    public abstract RespuestaHttp atender(Peticion peticion);

    protected static Consultor consultor(Peticion peticion) {
        Sesion sesion = peticion.getSesion();
        return sesion == null ? null : sesion.get(Sesion.CONSULTOR);
    }

    protected static Candidato candidato(Peticion peticion) {
        Sesion sesion = peticion.getSesion();
        return sesion == null ? null : sesion.get(Sesion.CANDIDATO);
    }

    protected static RespuestaHttp pagina(Peticion peticion, String titulo, String contenido) {
        if (peticion.esPost()) {
            // la página responde a un formulario: con Atrás o Actualizar se vuelve a pedir por GET
            contenido += Html.reemplazarHistorial(direccionGet(peticion));
        }
        return RespuestaHttp.html(Html.pagina(titulo, peticion.getSesion(), contenido));
    }

    private static String direccionGet(Peticion peticion) {
        String ruta = peticion.getRuta();
        if (ruta.startsWith("/preguntas/baja")) {
            return "/preguntas?refrescar=1";
        }
        Integer id = peticion.paramEntero("id");
        if (ruta.startsWith("/preguntas/modificar") && id != null) {
            return "/preguntas/modificar?id=" + id;
        }
        return ruta;
    }

    /** Las pantallas del consultor requieren un consultor autenticado (CU001). */
    protected static boolean esConsultor(Peticion peticion) {
        return consultor(peticion) != null;
    }
}
