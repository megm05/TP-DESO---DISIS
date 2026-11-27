package com.grupo12.presentacion;

import com.grupo12.dto.CompetenciaDTO;
import com.grupo12.dto.DatosPreguntaDTO;
import com.grupo12.dto.DetalleOpcionDTO;
import com.grupo12.dto.FactorDTO;
import com.grupo12.dto.OpcionRespuestaDTO;
import com.grupo12.gestores.Gestor_de_competencia;
import com.grupo12.gestores.Gestor_de_factor;
import com.grupo12.gestores.Gestor_de_pregunta;
import com.grupo12.gestores.Gestor_de_respuesta;
import com.grupo12.gestores.ValidacionException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Pantalla de alta de preguntas (CU021). */
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
        String texto = "<p>La Pregunta <strong>" + Html.e(datos.getNombre())
                + "</strong> se ha creado correctamente. ¿Desea cargar otra?</p>";
        String botones = Html.enlace("NO", "/preguntas?refrescar=1", "")
                + "<a class=\"boton boton-principal\" href=\"/preguntas/alta\" autofocus>SI</a>";
        return formulario(peticion, new DatosPreguntaDTO(), false, Collections.emptyList(), null,
                Html.emergente(Html.EXITO, "Aviso", texto, botones, "/preguntas?refrescar=1"));
    }

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

    /** Formulario de la pregunta. */
    protected static RespuestaHttp formulario(Peticion peticion, DatosPreguntaDTO datos, boolean modificar,
            List<String> errores, String foco) {
        return formulario(peticion, datos, modificar, errores, foco, null);
    }

    private static RespuestaHttp formulario(Peticion peticion, DatosPreguntaDTO datos, boolean modificar,
            List<String> errores, String foco, String emergente) {
        String ruta = modificar ? "/preguntas/modificar" : "/preguntas/alta";
        Map<String, String> marcas = marcasDeError(errores);
        // flujo 2.C: la pregunta ya existe; se avisa en una ventana emergente (ME022.1)
        boolean existente = "nombre".equals(foco) && !errores.isEmpty();
        StringBuilder sb = new StringBuilder();
        if (!existente) {
            // flujos 2.A y 2.B: panel con todas las omisiones, sin tapar campos ni botones
            sb.append(Html.errores(errores));
        }
        sb.append("<form method=\"post\" action=\"").append(ruta).append("\" novalidate>");
        if (modificar) {
            sb.append("<input type=\"hidden\" name=\"id\" value=\"").append(datos.getId_pregunta()).append("\">");
        }
        sb.append("<div class=\"form-columnas\"><div>");

        if (modificar) {
            sb.append("<p class=\"campo-dato\">Competencia: <strong>").append(Html.e(datos.getNombre_competencia()))
                    .append("</strong></p><p class=\"campo-dato\">Factor: <strong>")
                    .append(Html.e(datos.getNombre_factor())).append("</strong></p>");
        } else {
            sb.append(abrirCampo("competencia", marcas))
                    .append("<label for=\"competencia\">Competencia ").append(OBLIGATORIO).append("</label>")
                    .append("<select id=\"competencia\" name=\"competencia\" data-autoenviar=\"competencia\"")
                    .append(foco == null && datos.getId_competencia() == 0 && emergente == null ? " autofocus" : "")
                    .append("><option value=\"0\">(Seleccione una competencia)</option>");
            for (CompetenciaDTO competencia : new Gestor_de_competencia().buscar(null)) {
                sb.append(opcion(competencia.getId_competencia(), competencia.getNombre() + " ("
                        + competencia.getTipo() + ")", datos.getId_competencia()));
            }
            sb.append("</select></div>").append(abrirCampo("factor", marcas))
                    .append("<label for=\"factor\">Factor ").append(OBLIGATORIO).append("</label>")
                    .append("<select id=\"factor\" name=\"factor\"");
            if (datos.getId_competencia() == 0) {
                sb.append(" disabled><option value=\"0\">(Primero seleccione la competencia)</option>");
            } else {
                sb.append("><option value=\"0\">(Seleccione un factor)</option>");
                for (FactorDTO factor : new Gestor_de_factor().obtener_por_competencia(datos.getId_competencia())) {
                    sb.append(opcion(factor.getId_factor(), factor.getNombre(), datos.getId_factor()));
                }
            }
            sb.append("</select></div>");
        }
        sb.append(abrirCampo("nombre", marcas))
                .append("<label for=\"nombre\">Nombre de Pregunta ").append(OBLIGATORIO).append("</label>")
                .append(Html.texto("nombre", datos.getNombre(), Gestor_de_pregunta.LONGITUD_MAX_NOMBRE,
                        "nombre".equals(foco) ? "data-foco" : null)).append("</div>")
                .append(abrirCampo("pregunta", marcas))
                .append("<label for=\"pregunta\">Pregunta ").append(OBLIGATORIO).append("</label>")
                .append("<textarea id=\"pregunta\" name=\"pregunta\" class=\"mayusculas\" maxlength=\"")
                .append(Gestor_de_pregunta.LONGITUD_MAX_PREGUNTA).append("\">").append(Html.e(datos.getPregunta()))
                .append("</textarea></div>")
                .append(abrirCampo("descripcion", marcas))
                .append("<label for=\"descripcion\">Descripción <span class=\"opcional\">(opcional)</span></label>")
                .append("<textarea id=\"descripcion\" name=\"descripcion\" class=\"mayusculas\" maxlength=\"")
                .append(Gestor_de_pregunta.LONGITUD_MAX_DESCRIPCION).append("\">")
                .append(Html.e(datos.getDescripcion())).append("</textarea></div>");
        sb.append("</div><div>");

        if (!modificar) {
            sb.append("<div class=\"caja-ia\"><span>¿No tiene ganas de escribir?</span>")
                    .append("<button type=\"button\" disabled title=\"CU033 - Generar pregunta con IA: no forma parte")
                    .append(" de esta entrega\">Generar con IA</button></div>");
        }
        Gestor_de_respuesta gestorRespuesta = new Gestor_de_respuesta();
        sb.append(abrirCampo("opcion", marcas))
                .append("<label for=\"opcion\">Opción de Respuesta ").append(OBLIGATORIO).append("</label>")
                .append("<select id=\"opcion\" name=\"opcion\" data-autoenviar=\"opcion\">")
                .append("<option value=\"0\">(Seleccione una opción de respuesta)</option>");
        for (OpcionRespuestaDTO opcion : gestorRespuesta.buscar(null)) {
            sb.append(opcion(opcion.getId_opcion(), opcion.getNombre(), datos.getId_opcion()));
        }
        sb.append("</select></div>");
        if (datos.getId_opcion() > 0) {
            List<DetalleOpcionDTO> detalles = gestorRespuesta.obtener_detalles(datos.getId_opcion());
            sb.append(abrirCampo("ponderaciones", marcas)).append("<table class=\"grilla\"><thead><tr>")
                    .append("<th class=\"centro\">Orden</th><th>Descripción</th><th class=\"numero\">Ponderación</th>")
                    .append("</tr></thead><tbody>");
            for (DetalleOpcionDTO detalle : detalles) {
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
                    .append("</tfoot></table><p class=\"nota\">Las ponderaciones deben sumar 10 puntos y ser 0 o")
                    .append(" positivas.</p></div>");
        }
        sb.append("</div></div><p class=\"obligatorios\">").append(OBLIGATORIO).append(" Campos obligatorios.</p>");

        sb.append("<div class=\"acciones\"><a class=\"boton\" href=\"/preguntas?refrescar=1\">Cancelar</a>")
                .append("<button type=\"submit\" name=\"accion\" value=\"aceptar\" class=\"boton-principal\">Aceptar</button>")
                .append("</div></form>");

        if (existente) {
            // ME022.1: al presionar Cancelar se vuelve al paso 2 con el foco en el campo Nombre
            StringBuilder texto = new StringBuilder();
            for (String error : errores) {
                texto.append("<p>").append(Html.e(error)).append("</p>");
            }
            emergente = Html.emergente(Html.ADVERTENCIA, "Aviso", texto.toString(), Html.botonCerrar("Cancelar", true),
                    null);
        }
        if (emergente != null) {
            sb.append(emergente);
        }
        return pagina(peticion, modificar ? "Modificar Pregunta" : "Dar de alta Pregunta", sb.toString());
    }

    private static final String OBLIGATORIO = "<span class=\"obligatorio\">(*)</span>";

    private static String abrirCampo(String campo, Map<String, String> marcas) {
        String marca = marcas.get(campo);
        return marca == null ? "<div class=\"campo\">" : "<div class=\"campo con-error\">" + Html.errorCampo(marca);
    }

    private static Map<String, String> marcasDeError(List<String> errores) {
        Map<String, String> marcas = new HashMap<>();
        for (String error : errores) {
            String campo = campoDelError(error);
            if (campo == null || marcas.containsKey(campo)) {
                continue;
            }
            String marca;
            if ("ponderaciones".equals(campo)) {
                marca = "Revise las ponderaciones";
            } else if (error.startsWith("Debe")) {
                marca = "Debe completar este campo";
            } else if (error.startsWith("Ya existe")) {
                marca = "nombre".equals(campo) ? "Nombre ya existente" : "Pregunta ya existente";
            } else {
                marca = "Longitud máxima excedida";
            }
            marcas.put(campo, marca);
        }
        return marcas;
    }

    private static String campoDelError(String error) {
        if (error.startsWith("Debe seleccionar la Competencia")) {
            return "competencia";
        }
        if (error.startsWith("Debe seleccionar el Factor")) {
            return "factor";
        }
        if (error.startsWith("Debe ingresar el Nombre de Pregunta") || error.startsWith("El campo Nombre de Pregunta ")
                || error.startsWith("Ya existe una pregunta con el nombre")) {
            return "nombre";
        }
        if (error.startsWith("Debe ingresar la Pregunta") || error.startsWith("El campo Pregunta ")
                || error.startsWith("Ya existe una pregunta con el mismo texto")) {
            return "pregunta";
        }
        if (error.startsWith("El campo Descripción ")) {
            return "descripcion";
        }
        if (error.startsWith("Debe seleccionar la Opción de Respuesta")) {
            return "opcion";
        }
        if (error.contains("ponderaci")) {
            return "ponderaciones";
        }
        return null;
    }

    private static String opcion(int valor, String texto, int seleccionado) {
        return "<option value=\"" + valor + "\"" + (valor == seleccionado ? " selected" : "") + ">"
                + Html.e(texto) + "</option>";
    }
}
