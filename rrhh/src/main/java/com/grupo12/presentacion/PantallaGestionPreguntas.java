package com.grupo12.presentacion;

import com.grupo12.dominio.Consultor;
import com.grupo12.dto.PreguntaBusquedaDTO;
import com.grupo12.gestores.Gestor_de_pregunta;
import com.grupo12.gestores.ValidacionException;
import java.util.Collections;
import java.util.List;

/**
 * Pantalla de gestión de preguntas (CU020). Desde aquí se invocan el alta (CU021),
 * la modificación (CU022) y la baja (CU023); la confirmación de la baja también se
 * presenta en esta pantalla.
 */
public class PantallaGestionPreguntas extends Pantalla {

    /** Últimos criterios de búsqueda, para actualizar la grilla al volver de otro caso de uso. */
    static final String CRITERIOS = "preguntas.criterios";

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        Consultor consultor = consultor(peticion);
        if (consultor == null) {
            return RespuestaHttp.redirigir("/login");
        }
        if (peticion.getRuta().startsWith("/preguntas/baja")) {
            return darDeBaja(peticion, consultor);
        }
        return buscar(peticion);
    }

    // ------------------------------------------------------------------
    //  CU020 - Gestionar Preguntas
    // ------------------------------------------------------------------

    private RespuestaHttp buscar(Peticion peticion) {
        Sesion sesion = peticion.getSesion();
        String[] criterios;
        boolean buscar;
        if ("1".equals(peticion.param("refrescar"))) {
            // al volver del alta, la modificación o la baja se actualiza la grilla
            criterios = sesion.get(CRITERIOS);
            buscar = criterios != null;
        } else {
            criterios = new String[] {peticion.param("competencia"), peticion.param("factor"),
                peticion.param("nombre")};
            buscar = "1".equals(peticion.param("buscar"));
        }
        if (criterios == null) {
            criterios = new String[] {"", "", ""};
        }

        List<String> errores = Collections.emptyList();
        List<PreguntaBusquedaDTO> preguntas = null;
        if (buscar) {
            try {
                preguntas = new Gestor_de_pregunta().buscar(criterios[0], criterios[1], criterios[2]);
                sesion.put(CRITERIOS, criterios);
            } catch (ValidacionException e) {
                errores = e.getErrores();
            }
        }
        String error = peticion.param("error");
        if ("seleccion".equals(error)) {
            errores = Collections.singletonList("Debe seleccionar una pregunta de la lista.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(Html.mensajes(sesion.tomarMensajes())).append(Html.errores(errores));
        sb.append("<form method=\"get\" action=\"/preguntas\" class=\"filtros\">")
                .append("<div class=\"campo\"><label for=\"competencia\">Competencia</label>")
                .append(Html.texto("competencia", criterios[0], 100, "autofocus")).append("</div>")
                .append("<div class=\"campo\"><label for=\"factor\">Factor</label>")
                .append(Html.texto("factor", criterios[1], 100, null)).append("</div>")
                .append("<div class=\"campo\"><label for=\"nombre\">Nombre de Pregunta</label>")
                .append(Html.texto("nombre", criterios[2], 100, null)).append("</div>")
                .append("<div><button type=\"submit\" name=\"buscar\" value=\"1\">Buscar</button></div>")
                .append("</form><p class=\"nota\">Ingrese datos en al menos uno de los criterios. La búsqueda"
                        + " encuentra las preguntas que <em>contienen</em> el texto ingresado.</p>");

        sb.append("<form method=\"get\" action=\"/preguntas/modificar\">");
        if (preguntas != null) {
            sb.append("<table class=\"grilla\"><thead><tr><th class=\"centro\"></th><th>Competencia</th>")
                    .append("<th>Factor</th><th>Pregunta</th></tr></thead><tbody>");
            if (preguntas.isEmpty()) {
                sb.append("<tr><td colspan=\"4\" class=\"vacia\">No se encontraron preguntas que coincidan"
                        + " con los criterios de búsqueda.</td></tr>");
            }
            for (PreguntaBusquedaDTO pregunta : preguntas) {
                sb.append("<tr class=\"seleccionable\"><td class=\"centro\"><input type=\"radio\" name=\"id\" value=\"")
                        .append(pregunta.getId_pregunta()).append("\" aria-label=\"Seleccionar\"></td><td>")
                        .append(Html.e(pregunta.getNombre_competencia())).append("</td><td>")
                        .append(Html.e(pregunta.getNombre_factor())).append("</td><td><strong>")
                        .append(Html.e(pregunta.getNombre_pregunta())).append("</strong><br><span class=\"nota\">")
                        .append(Html.e(pregunta.getPregunta())).append("</span></td></tr>");
            }
            sb.append("</tbody></table>");
        }
        sb.append("<div class=\"acciones\">")
                .append("<a class=\"boton\" href=\"/preguntas/alta\">Nuevo</a>");
        if (preguntas != null && !preguntas.isEmpty()) {
            sb.append("<button type=\"submit\" class=\"boton-secundario\">Modificar</button>")
                    .append("<button type=\"submit\" class=\"boton-peligro\" formaction=\"/preguntas/baja\">Eliminar</button>");
        }
        sb.append("<a class=\"boton boton-secundario\" href=\"/principal\">Salir</a></div></form>");
        return pagina(peticion, "Gestionar Preguntas", sb.toString());
    }

    // ------------------------------------------------------------------
    //  CU023 - Dar de baja Pregunta
    // ------------------------------------------------------------------

    private RespuestaHttp darDeBaja(Peticion peticion, Consultor consultor) {
        Integer id = peticion.paramEntero("id");
        if (id == null) {
            return RespuestaHttp.redirigir("/preguntas?refrescar=1&error=seleccion");
        }
        Gestor_de_pregunta gestor = new Gestor_de_pregunta();
        String nombre;
        try {
            nombre = gestor.obtener_datos(id).getNombre();
        } catch (ValidacionException e) {
            peticion.getSesion().agregarMensaje(e.getMessage());
            return RespuestaHttp.redirigir("/preguntas?refrescar=1");
        }

        if (!peticion.esPost()) {
            // paso 2: mensaje de confirmación con los botones ELIMINAR y CANCELAR
            String contenido = "<div class=\"panel panel-aviso\"><p>Los datos de la pregunta <strong>"
                    + Html.e(nombre) + "</strong> serán eliminados del sistema.</p></div>"
                    + "<form method=\"post\" action=\"/preguntas/baja\">"
                    + "<input type=\"hidden\" name=\"id\" value=\"" + id + "\">"
                    + "<div class=\"acciones\">"
                    + "<button type=\"submit\" name=\"accion\" value=\"eliminar\" class=\"boton-peligro\" autofocus>ELIMINAR</button>"
                    + "<button type=\"submit\" name=\"accion\" value=\"cancelar\" class=\"boton-secundario\">CANCELAR</button>"
                    + "</div></form>";
            return pagina(peticion, "Dar de baja Pregunta", contenido);
        }
        if (!"eliminar".equals(peticion.param("accion"))) {
            // flujo 3.A: el actor presiona CANCELAR y el caso de uso termina
            return RespuestaHttp.redirigir("/preguntas?refrescar=1");
        }
        // paso 3: baja lógica con registro de auditoría
        gestor.dar_baja(id, consultor);
        String contenido = Html.exito("Los datos de la pregunta " + nombre + " han sido eliminados del sistema.")
                + "<p class=\"nota\" data-tecla-continuar=\"/preguntas?refrescar=1\">Presione cualquier tecla"
                + " para continuar.</p>";
        return pagina(peticion, "Dar de baja Pregunta", contenido);
    }
}
