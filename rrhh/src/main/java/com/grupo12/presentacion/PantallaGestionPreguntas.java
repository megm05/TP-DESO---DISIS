package com.grupo12.presentacion;

import com.grupo12.dominio.Consultor;
import com.grupo12.dto.PreguntaBusquedaDTO;
import com.grupo12.gestores.Gestor_de_pregunta;
import com.grupo12.gestores.ValidacionException;
import java.util.Collections;
import java.util.List;

/** Pantalla de gestión de preguntas (CU020). */
public class PantallaGestionPreguntas extends Pantalla {

    static final String CRITERIOS = "preguntas.criterios";
    private static final int FILAS_POR_PAGINA = 10;

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        Consultor consultor = consultor(peticion);
        if (consultor == null) {
            return RespuestaHttp.redirigir("/login");
        }
        if (peticion.getRuta().startsWith("/preguntas/baja")) {
            return darDeBaja(peticion, consultor);
        }
        // al volver del alta, la modificación o la baja se actualiza la grilla
        return buscar(peticion, "1".equals(peticion.param("refrescar")), null);
    }

    private RespuestaHttp buscar(Peticion peticion, boolean refrescar, String emergente) {
        Sesion sesion = peticion.getSesion();
        String[] criterios;
        boolean buscar;
        if (refrescar) {
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
        if ("seleccion".equals(peticion.param("error"))) {
            errores = Collections.singletonList("Debe seleccionar una pregunta de la lista.");
        }

        StringBuilder sb = new StringBuilder(Html.errores(errores));
        sb.append("<form method=\"get\" action=\"/preguntas\"><fieldset class=\"recuadro\">")
                .append("<legend>Filtros de búsqueda</legend><div class=\"filtros\"><div class=\"filtros-campos\">")
                .append("<label for=\"competencia\">Competencia:</label>")
                .append(Html.texto("competencia", criterios[0], 100, emergente == null ? "autofocus" : null))
                .append("<label for=\"factor\">Factor:</label>").append(Html.texto("factor", criterios[1], 100, null))
                .append("<label for=\"nombre\">Nombre de pregunta:</label>")
                .append(Html.texto("nombre", criterios[2], 100, null))
                .append("</div><div><button type=\"submit\" name=\"buscar\" value=\"1\" class=\"boton-grande\">")
                .append(Html.icono("lupa", 16)).append("Buscar</button></div>")
                .append("<div class=\"filtros-pie\"><a class=\"boton\" href=\"/principal\">Salir</a>")
                .append("<span class=\"nota\">Ingrese datos en al menos uno de los criterios. La búsqueda encuentra")
                .append(" las preguntas que <em>contienen</em> el texto ingresado.</span></div>")
                .append("</div></fieldset></form>")
                .append("<div class=\"acciones centro\"><a class=\"boton boton-grande\" href=\"/preguntas/alta\">")
                .append("Nuevo</a></div>");

        int pagina = 1;
        if (preguntas != null) {
            int paginas = Math.max(1, (preguntas.size() + FILAS_POR_PAGINA - 1) / FILAS_POR_PAGINA);
            Integer pedida = peticion.paramEntero("pag");
            pagina = pedida == null ? 1 : Math.max(1, Math.min(paginas, pedida));
            List<PreguntaBusquedaDTO> visibles = preguntas.subList((pagina - 1) * FILAS_POR_PAGINA,
                    Math.min(preguntas.size(), pagina * FILAS_POR_PAGINA));
            sb.append("<h2 class=\"seccion\">Preguntas (").append(preguntas.size()).append(")</h2>")
                    .append("<table class=\"grilla\"><thead><tr><th>Competencia</th><th>Factor</th><th>Pregunta</th>")
                    .append("<th class=\"accion\" title=\"Seleccionar\"></th></tr></thead><tbody>");
            if (preguntas.isEmpty()) {
                sb.append("<tr><td colspan=\"4\" class=\"vacia\">No se encontraron preguntas que coincidan"
                        + " con los criterios de búsqueda.</td></tr>");
            }
            for (PreguntaBusquedaDTO pregunta : visibles) {
                sb.append("<tr><td>").append(Html.e(pregunta.getNombre_competencia())).append("</td><td>")
                        .append(Html.e(pregunta.getNombre_factor())).append("</td><td><strong>")
                        .append(Html.e(pregunta.getNombre_pregunta())).append("</strong><br><span class=\"nota\">")
                        .append(Html.e(pregunta.getPregunta())).append("</span></td><td class=\"accion\">")
                        .append("<a class=\"boton boton-icono\" href=\"/preguntas?refrescar=1&amp;pag=").append(pagina)
                        .append("&amp;sel=").append(pregunta.getId_pregunta()).append("\" title=\"Seleccionar\"")
                        .append(" aria-label=\"Seleccionar ").append(Html.e(pregunta.getNombre_pregunta())).append("\">")
                        .append(Html.icono("lapiz", 18)).append("</a></td></tr>");
            }
            sb.append("</tbody></table>").append(Html.paginacion(pagina, paginas, "/preguntas?refrescar=1"));
        }

        Integer seleccionada = peticion.paramEntero("sel");
        if (emergente == null && seleccionada != null && preguntas != null) {
            for (PreguntaBusquedaDTO pregunta : preguntas) {
                if (pregunta.getId_pregunta() == seleccionada) {
                    emergente = ventanaSeleccion(pregunta, "/preguntas?refrescar=1&pag=" + pagina);
                }
            }
        }
        if (emergente == null) {
            emergente = Html.mensajesEmergentes(sesion.tomarMensajes());
        }
        sb.append(emergente);
        return pagina(peticion, "Gestionar Preguntas", sb.toString());
    }

    private static String ventanaSeleccion(PreguntaBusquedaDTO pregunta, String volver) {
        int id = pregunta.getId_pregunta();
        String texto = "<div class=\"campo\"><label for=\"seleccionada\">Pregunta seleccionada:</label>"
                + "<input type=\"text\" id=\"seleccionada\" readonly value=\"" + Html.e(pregunta.getNombre_pregunta())
                + "\"></div><p class=\"nota\">" + Html.e(pregunta.getNombre_competencia()) + " / "
                + Html.e(pregunta.getNombre_factor()) + "<br>" + Html.e(pregunta.getPregunta()) + "</p>";
        String botones = Html.enlace("Cancelar", volver, "izquierda")
                + Html.enlace("Eliminar", "/preguntas/baja?id=" + id, "boton-peligro")
                + "<a class=\"boton boton-principal\" href=\"/preguntas/modificar?id=" + id + "\" autofocus>Modificar</a>";
        return Html.emergente(null, "Pregunta seleccionada", texto, botones, volver);
    }

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
            // paso 2: mensaje emergente ME023.1 con los botones CANCELAR y ELIMINAR
            String texto = "<p>Los datos de la pregunta <strong>" + Html.e(nombre)
                    + "</strong> serán eliminados del sistema.</p>";
            String botones = "<form method=\"post\" action=\"/preguntas/baja\">"
                    + "<input type=\"hidden\" name=\"id\" value=\"" + id + "\">"
                    + "<button type=\"submit\" name=\"accion\" value=\"cancelar\">CANCELAR</button>"
                    + "<button type=\"submit\" name=\"accion\" value=\"eliminar\" class=\"boton-peligro\" autofocus>"
                    + "ELIMINAR</button></form>";
            return buscar(peticion, true, Html.emergente(Html.ADVERTENCIA, "Dar de baja Pregunta", texto, botones,
                    "/preguntas?refrescar=1"));
        }
        if (!"eliminar".equals(peticion.param("accion"))) {
            // flujo 3.A: el actor presiona CANCELAR y el caso de uso termina
            return RespuestaHttp.redirigir("/preguntas?refrescar=1");
        }
        // paso 3: baja lógica con registro de auditoría
        gestor.dar_baja(id, consultor);
        // ME023.2: la notificación desaparece al presionar cualquier tecla o botón
        String texto = "<p>Los datos de la pregunta <strong>" + Html.e(nombre)
                + "</strong> han sido eliminados del sistema.</p>"
                + "<p class=\"nota\" data-tecla-continuar=\"/preguntas?refrescar=1\">Presione cualquier tecla"
                + " para continuar.</p>";
        return buscar(peticion, true, Html.emergente(Html.EXITO, "Dar de baja Pregunta", texto, "", ""));
    }
}
