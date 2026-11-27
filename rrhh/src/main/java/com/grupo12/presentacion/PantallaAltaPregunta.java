package com.grupo12.presentacion;

import com.grupo12.dominio.Competencia;
import com.grupo12.dominio.Detalle_opcion;
import com.grupo12.dominio.Factor;
import com.grupo12.dominio.Opcion_respuesta;
import com.grupo12.dto.DatosPreguntaDTO;
import com.grupo12.gestores.Gestor_de_competencia;
import com.grupo12.gestores.Gestor_de_factor;
import com.grupo12.gestores.Gestor_de_pregunta;
import com.grupo12.gestores.Gestor_de_respuesta;
import com.grupo12.gestores.ValidacionException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Pantalla de alta de preguntas (CU021). La pantalla de modificación (CU022)
 * reutiliza el mismo formulario con la competencia y el factor de solo lectura.
 */
public class PantallaAltaPregunta extends Pantalla {

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        if (!esConsultor(peticion)) {
            return RespuestaHttp.redirigir("/login");
        }
        if (!peticion.esPost()) {
            // paso 1: pantalla de alta en blanco
            return formulario(peticion, new DatosPreguntaDTO(), false, Collections.emptyList(), null);
        }
        DatosPreguntaDTO datos = leerDatos(peticion);
        String accion = peticion.param("accion");
        if ("competencia".equals(accion)) {
            // al seleccionar una competencia se cargan sus factores
            datos.setId_factor(0);
            return formulario(peticion, datos, false, Collections.emptyList(), null);
        }
        if ("opcion".equals(accion)) {
            // al seleccionar la opción de respuesta se muestra la grilla de opciones
            datos.getPonderaciones().clear();
            return formulario(peticion, datos, false, Collections.emptyList(), null);
        }
        try {
            // paso 2: el actor presiona "Aceptar"
            new Gestor_de_pregunta().dar_de_alta(datos);
        } catch (ValidacionException e) {
            // flujos 2.A, 2.B y 2.C: se informan TODOS los errores y el CU vuelve al paso 2
            return formulario(peticion, datos, false, e.getErrores(), e.getCampo());
        }
        // pasos 3 y 4: mensaje de éxito con los botones SI y NO
        String contenido = Html.exito("La Pregunta " + datos.getNombre()
                + " se ha creado correctamente. ¿Desea cargar otra?")
                + "<div class=\"acciones\">"
                + "<a class=\"boton\" href=\"/preguntas/alta\" autofocus>SI</a>"
                + "<a class=\"boton boton-secundario\" href=\"/preguntas?refrescar=1\">NO</a></div>";
        return pagina(peticion, "Dar de alta Pregunta", contenido);
    }

    /** Arma el DTO con los datos que el actor ingresó en el formulario. */
    protected static DatosPreguntaDTO leerDatos(Peticion peticion) {
        DatosPreguntaDTO datos = new DatosPreguntaDTO();
        datos.setId_pregunta(peticion.paramEnteroOCero("id"));
        datos.setId_competencia(peticion.paramEnteroOCero("competencia"));
        datos.setId_factor(peticion.paramEnteroOCero("factor"));
        datos.setNombre(peticion.param("nombre"));
        datos.setPregunta(peticion.param("pregunta"));
        datos.setDescripcion(peticion.param("descripcion"));
        datos.setId_opcion(peticion.paramEnteroOCero("opcion"));
        Map<Integer, Double> ponderaciones = datos.getPonderaciones();
        for (String nombre : peticion.nombresConPrefijo("pond_")) {
            try {
                int id_detalle = Integer.parseInt(nombre.substring("pond_".length()));
                String valor = peticion.param(nombre).replace(',', '.');
                ponderaciones.put(id_detalle, valor.isEmpty() ? null : Double.valueOf(valor));
            } catch (NumberFormatException e) {
                ponderaciones.put(parseId(nombre), null);
            }
        }
        return datos;
    }

    private static int parseId(String nombre) {
        try {
            return Integer.parseInt(nombre.substring("pond_".length()));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Formulario de la pregunta.
     *
     * @param modificar true para la modificación (CU022): competencia y factor de solo lectura
     * @param foco      campo que recibe el foco (por ejemplo "nombre" en el flujo 2.C); puede ser null
     */
    protected static RespuestaHttp formulario(Peticion peticion, DatosPreguntaDTO datos, boolean modificar,
            List<String> errores, String foco) {
        String ruta = modificar ? "/preguntas/modificar" : "/preguntas/alta";
        StringBuilder sb = new StringBuilder();
        sb.append(Html.errores(errores));
        sb.append("<form method=\"post\" action=\"").append(ruta).append("\" class=\"formulario\" novalidate>");
        if (modificar) {
            sb.append("<input type=\"hidden\" name=\"id\" value=\"").append(datos.getId_pregunta()).append("\">");
        }

        // Competencia y factor
        if (modificar) {
            sb.append("<label>Competencia</label><input type=\"text\" readonly value=\"")
                    .append(Html.e(datos.getNombre_competencia())).append("\">")
                    .append("<label>Factor</label><input type=\"text\" readonly value=\"")
                    .append(Html.e(datos.getNombre_factor())).append("\">");
        } else {
            sb.append("<label for=\"competencia\">Competencia</label>")
                    .append("<select id=\"competencia\" name=\"competencia\" data-autoenviar=\"competencia\"")
                    .append(foco == null && datos.getId_competencia() == 0 ? " autofocus" : "").append(">")
                    .append("<option value=\"0\">(Seleccione una competencia)</option>");
            for (Competencia competencia : new Gestor_de_competencia().buscar(null)) {
                sb.append(opcion(competencia.getId_competencia(), competencia.getNombre() + " ("
                        + competencia.getTipo() + ")", datos.getId_competencia()));
            }
            sb.append("</select><label for=\"factor\">Factor</label><select id=\"factor\" name=\"factor\"");
            if (datos.getId_competencia() == 0) {
                sb.append(" disabled><option value=\"0\">(Primero seleccione la competencia)</option>");
            } else {
                sb.append("><option value=\"0\">(Seleccione un factor)</option>");
                for (Factor factor : new Gestor_de_factor().obtener_por_competencia(datos.getId_competencia())) {
                    sb.append(opcion(factor.getId_factor(), factor.getNombre(), datos.getId_factor()));
                }
            }
            sb.append("</select>");
        }

        // Datos de la pregunta
        sb.append("<label for=\"nombre\">Nombre de Pregunta</label>")
                .append(Html.texto("nombre", datos.getNombre(), Gestor_de_pregunta.LONGITUD_MAX_NOMBRE,
                        "nombre".equals(foco) ? "data-foco" : null))
                .append("<label for=\"pregunta\">Pregunta</label>")
                .append("<textarea id=\"pregunta\" name=\"pregunta\" class=\"mayusculas\" maxlength=\"")
                .append(Gestor_de_pregunta.LONGITUD_MAX_PREGUNTA).append("\">").append(Html.e(datos.getPregunta()))
                .append("</textarea>")
                .append("<label for=\"descripcion\">Descripción <span class=\"opcional\">(opcional)</span></label>")
                .append("<textarea id=\"descripcion\" name=\"descripcion\" class=\"mayusculas\" maxlength=\"")
                .append(Gestor_de_pregunta.LONGITUD_MAX_DESCRIPCION).append("\">")
                .append(Html.e(datos.getDescripcion())).append("</textarea>");

        // Opción de respuesta y grilla de ponderaciones
        Gestor_de_respuesta gestorRespuesta = new Gestor_de_respuesta();
        sb.append("<label for=\"opcion\">Opción de Respuesta</label>")
                .append("<select id=\"opcion\" name=\"opcion\" data-autoenviar=\"opcion\">")
                .append("<option value=\"0\">(Seleccione una opción de respuesta)</option>");
        for (Opcion_respuesta opcion : gestorRespuesta.buscar(null)) {
            sb.append(opcion(opcion.getId_opcion(), opcion.getNombre(), datos.getId_opcion()));
        }
        sb.append("</select>");
        if (datos.getId_opcion() > 0) {
            List<Detalle_opcion> detalles = gestorRespuesta.obtener_detalles(datos.getId_opcion());
            sb.append("<div class=\"ancho-completo\"><table class=\"grilla\"><thead><tr>")
                    .append("<th class=\"centro\">Orden</th><th>Descripción</th><th class=\"numero\">Ponderación</th>")
                    .append("</tr></thead><tbody>");
            for (Detalle_opcion detalle : detalles) {
                Double ponderacion = datos.getPonderaciones().get(detalle.getId_detalle());
                sb.append("<tr><td class=\"centro\">").append(detalle.getOrden()).append("</td><td>")
                        .append(Html.e(detalle.getDescripcion())).append("</td><td class=\"numero\">")
                        .append("<input type=\"text\" inputmode=\"decimal\" class=\"ponderacion\" name=\"pond_")
                        .append(detalle.getId_detalle()).append("\" value=\"")
                        .append(ponderacion == null ? "" : Html.numero(ponderacion))
                        .append("\" aria-label=\"Ponderación de ").append(Html.e(detalle.getDescripcion()))
                        .append("\"></td></tr>");
            }
            sb.append("</tbody><tfoot><tr><td></td><td class=\"numero\"><strong>Total</strong></td>")
                    .append("<td class=\"numero\"><span id=\"suma-ponderaciones\" class=\"suma\">0</span></td></tr>")
                    .append("</tfoot></table><p class=\"nota\">Las ponderaciones deben sumar 10 puntos y ser 0 o positivas.</p></div>");
        }

        // Botones
        sb.append("<div class=\"acciones\">");
        if (!modificar) {
            sb.append("<button type=\"button\" class=\"boton-secundario\" disabled"
                    + " title=\"CU033 - Generar pregunta con IA: no forma parte de esta entrega\">Generar con IA</button>");
        }
        sb.append("<button type=\"submit\" name=\"accion\" value=\"aceptar\">Aceptar</button>")
                .append("<a class=\"boton boton-secundario\" href=\"/preguntas?refrescar=1\">Cancelar</a>")
                .append("</div></form>");
        return pagina(peticion, modificar ? "Modificar Pregunta" : "Dar de alta Pregunta", sb.toString());
    }

    private static String opcion(int valor, String texto, int seleccionado) {
        return "<option value=\"" + valor + "\"" + (valor == seleccionado ? " selected" : "") + ">"
                + Html.e(texto) + "</option>";
    }
}
