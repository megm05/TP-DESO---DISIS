package com.grupo24.presentacion;

import com.grupo24.dominio.Candidato;
import com.grupo24.dominio.Consultor;
import com.grupo24.dominio.Tipo_doc;
import com.grupo24.gestores.Gestor_de_autenticacion;
import com.grupo24.util.Texto;

/** Pantalla de autenticación (CU001). */
public class PantallaLogin extends Pantalla {

    /** Longitudes máximas definidas por el LDAP (CU001, requerimientos especiales). */
    private static final int MAX_USUARIO = 15;
    private static final int MAX_CONTRASENIA = 20;

    private static final String ERROR_CONSULTOR = "El usuario o la contraseña no son válidos";
    private static final String ERROR_CANDIDATO =
            "Los datos ingresados no son válidos o no existe un cuestionario para el Candidato";

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        if (peticion.getRuta().startsWith("/salir")) {
            peticion.cerrarSesion();
            return RespuestaHttp.redirigir("/login");
        }
        boolean candidato = "candidato".equals(peticion.param("tipo"));
        if (!peticion.esPost()) {
            return formulario(peticion, candidato, null);
        }
        Gestor_de_autenticacion gestor = new Gestor_de_autenticacion();
        if (candidato) {
            // flujo 3.A: realizar cuestionario
            Tipo_doc tipo_doc = tipoDocumento(peticion.param("tipo_doc"));
            Candidato autenticado = gestor.autenticar_candidato(tipo_doc,
                    Texto.mayusculas(peticion.param("nro_doc")), peticion.param("clave"));
            if (autenticado == null) {
                return formulario(peticion, true, ERROR_CANDIDATO);
            }
            peticion.cerrarSesion();
            peticion.obtenerOCrearSesion().put(Sesion.CANDIDATO, autenticado);
            return RespuestaHttp.redirigir("/cuestionario");
        }
        // flujo principal: ingresar como usuario consultor
        Consultor consultor = gestor.autenticar_consultor(Texto.mayusculas(peticion.param("usuario")),
                peticion.param("contrasenia"));
        if (consultor == null) {
            return formulario(peticion, false, ERROR_CONSULTOR);
        }
        peticion.cerrarSesion();
        peticion.obtenerOCrearSesion().put(Sesion.CONSULTOR, consultor);
        return RespuestaHttp.redirigir("/principal");
    }

    private static Tipo_doc tipoDocumento(String valor) {
        try {
            return Tipo_doc.valueOf(valor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static RespuestaHttp formulario(Peticion peticion, boolean candidato, String error) {
        StringBuilder sb = new StringBuilder("<div class=\"acceso\">");
        sb.append("<p class=\"introduccion\">Seleccione el tipo de acceso:</p><nav class=\"pestanas\">")
                .append("<a href=\"/login?tipo=consultor\"").append(candidato ? "" : " class=\"activa\"")
                .append(">Ingresar como usuario consultor</a>")
                .append("<a href=\"/login?tipo=candidato\"").append(candidato ? " class=\"activa\"" : "")
                .append(">Realizar cuestionario</a></nav>");
        if (error != null) {
            sb.append(Html.errores(java.util.Collections.singletonList(error)));
        }
        sb.append("<form method=\"post\" action=\"/login\" autocomplete=\"off\">");
        if (candidato) {
            sb.append("<input type=\"hidden\" name=\"tipo\" value=\"candidato\">")
                    .append("<div class=\"campo\"><label for=\"tipo_doc\">Tipo de documento</label>")
                    .append("<select id=\"tipo_doc\" name=\"tipo_doc\" autofocus>");
            for (Tipo_doc tipo : Tipo_doc.values()) {
                sb.append("<option value=\"").append(tipo.name()).append("\">").append(tipo.name()).append("</option>");
            }
            sb.append("</select></div>")
                    .append("<div class=\"campo\"><label for=\"nro_doc\">Número de documento</label>")
                    .append(Html.texto("nro_doc", "", 20, "required")).append("</div>")
                    .append("<div class=\"campo\"><label for=\"clave\">Clave</label>")
                    .append("<input type=\"password\" id=\"clave\" name=\"clave\" maxlength=\"8\" required></div>");
        } else {
            sb.append("<input type=\"hidden\" name=\"tipo\" value=\"consultor\">")
                    .append("<div class=\"campo\"><label for=\"usuario\">Nombre de usuario</label>")
                    .append(Html.texto("usuario", "", MAX_USUARIO, "required autofocus")).append("</div>")
                    .append("<div class=\"campo\"><label for=\"contrasenia\">Contraseña</label>")
                    .append("<input type=\"password\" id=\"contrasenia\" name=\"contrasenia\" maxlength=\"")
                    .append(MAX_CONTRASENIA).append("\" required></div>");
        }
        sb.append("<div class=\"acciones derecha\"><button type=\"submit\" class=\"boton-principal\">Ingresar</button>")
                .append("</div></form>");
        if (candidato) {
            sb.append("<p class=\"nota\">Ingrese el tipo y número de documento y la clave de ingreso que le"
                    + " entregó la consultora.</p>");
        }
        sb.append("</div>");
        if (peticion.esPost()) {
            // ingreso fallido: con Atrás o Actualizar no se pide reenviar los datos
            sb.append(Html.reemplazarHistorial(candidato ? "/login?tipo=candidato" : "/login"));
        }
        return RespuestaHttp.html(Html.pagina("Ingreso al sistema", null, sb.toString(), "angosta"));
    }
}
