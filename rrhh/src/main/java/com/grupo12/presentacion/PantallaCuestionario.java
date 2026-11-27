package com.grupo12.presentacion;

import com.grupo12.dominio.BloquePregunta;
import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Cuestionario;
import com.grupo12.dominio.Estado;
import com.grupo12.dominio.Pregunta;
import com.grupo12.dto.OpcionBloqueDTO;
import com.grupo12.dto.PreguntaBloqueDTO;
import com.grupo12.gestores.Gestor_de_cuestionario;
import com.grupo12.gestores.ValidacionException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Pantalla del CU026 - Completar Cuestionario. */
public class PantallaCuestionario extends Pantalla {

    private static final String INGRESO_REGISTRADO = "cuestionario.ingreso";
    private static final String AVISO = "cuestionario.aviso";
    private static final String COMPLETADO = "cuestionario.completado";
    private static final String FALTAN = "cuestionario.faltan";

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        Candidato candidato = candidato(peticion);
        if (candidato == null) {
            return RespuestaHttp.redirigir("/login?tipo=candidato");
        }
        Sesion sesion = peticion.getSesion();
        Gestor_de_cuestionario gestor = new Gestor_de_cuestionario();

        String aviso = sesion.get(AVISO);
        if (aviso != null) {
            return cierre(peticion, aviso);
        }
        if (Boolean.TRUE.equals(sesion.get(COMPLETADO))) {
            return completado(peticion);
        }
        Cuestionario cuestionario = gestor.obtener_cuestionario_activo(candidato);
        if (cuestionario == null) {
            return cierre(peticion, "No existe un cuestionario activo o en proceso para el candidato.");
        }
        if (sesion.get(INGRESO_REGISTRADO) == null) {
            // paso 1: control de vencimientos (flujos 1.A y 1.B) y de la cantidad de accesos
            sesion.put(INGRESO_REGISTRADO, Boolean.TRUE);
            String vencido = gestor.registrar_ingreso(cuestionario);
            if (vencido != null) {
                sesion.put(AVISO, vencido + " El cuestionario quedó en estado "
                        + cuestionario.getEstado_cuestionario().name().replace('_', ' ') + ".");
                return RespuestaHttp.redirigir("/cuestionario");
            }
        }

        if (peticion.esPost()) {
            return procesar(peticion, sesion, gestor, cuestionario);
        }
        if (cuestionario.getEstado_cuestionario() == Estado.ACTIVO) {
            return instrucciones(peticion, gestor, cuestionario, null);
        }
        // flujo 1.C: el cuestionario en proceso se presenta posicionado en el bloque no respondido
        return bloque(peticion, sesion, gestor, cuestionario);
    }

    private RespuestaHttp procesar(Peticion peticion, Sesion sesion, Gestor_de_cuestionario gestor,
            Cuestionario cuestionario) {
        String accion = peticion.param("accion");
        if ("cancelar".equals(accion)) {
            // flujo 2.A: el actor presiona Cancelar y el caso de uso termina
            peticion.cerrarSesion();
            return RespuestaHttp.redirigir("/login?tipo=candidato");
        }
        if ("aceptar".equals(accion)) {
            // paso 3: se asocian las preguntas al azar y el cuestionario pasa a "En Proceso"
            try {
                gestor.iniciar_cuestionario(cuestionario);
            } catch (ValidacionException e) {
                return instrucciones(peticion, gestor, cuestionario, e.getErrores());
            }
            return RespuestaHttp.redirigir("/cuestionario");
        }
        if ("responder".equals(accion) && cuestionario.getEstado_cuestionario() == Estado.EN_PROCESO) {
            // pasos 5 a 7
            Map<Integer, Integer> respuestas = new HashMap<>();
            for (String nombre : peticion.nombresConPrefijo("r_")) {
                try {
                    respuestas.put(Integer.valueOf(nombre.substring(2)), Integer.valueOf(peticion.param(nombre)));
                } catch (NumberFormatException e) {
                    // se ignora un valor mal formado: la pregunta queda sin responder
                }
            }
            try {
                List<Pregunta> sinResponder = gestor.registrar_respuestas_bloque(cuestionario, respuestas);
                if (!sinResponder.isEmpty()) {
                    // flujo 6.A: se mantiene el bloque con las respuestas ya cargadas
                    sesion.put(FALTAN, Boolean.TRUE);
                    return RespuestaHttp.redirigir("/cuestionario");
                }
            } catch (ValidacionException e) {
                sesion.agregarMensaje(e.getMessage());
                return RespuestaHttp.redirigir("/cuestionario");
            }
            if (cuestionario.obtener_bloque_no_respondido() == null) {
                // paso 8: último paso del wizard
                gestor.finalizar(cuestionario);
                sesion.put(COMPLETADO, Boolean.TRUE);
            }
            // flujo 8.A: si hay más bloques se presenta el siguiente
        }
        return RespuestaHttp.redirigir("/cuestionario");
    }

    /** Pasos del wizard, como en el wireframe: 1 Instrucciones - 2 Cuestionario. */
    private static final String[] PASOS = {"Instrucciones", "Cuestionario"};

    /** Pantalla "Cuestionario - Instrucciones" con los botones Cancelar y Aceptar (paso 1). */
    private static RespuestaHttp instrucciones(Peticion peticion, Gestor_de_cuestionario gestor,
            Cuestionario cuestionario, List<String> errores) {
        String contenido = Html.pasos(PASOS, 0) + Html.errores(errores)
                + "<dl class=\"datos-cuestionario\"><dt>Estado:</dt><dd>"
                + Html.e(cuestionario.getEstado_cuestionario().name().replace('_', ' ')) + "</dd>"
                + "<dt>Tiempo para completarlo:</dt><dd>" + Html.numero((double) cuestionario.getTiempoMax())
                + " horas a partir de que presione Aceptar</dd>"
                + "<dt>Accesos registrados:</dt><dd>" + cuestionario.getCant_accesos() + "</dd></dl>"
                + "<div class=\"instrucciones\">" + Html.e(gestor.obtener_instrucciones()) + "</div>"
                + "<form method=\"post\" action=\"/cuestionario\"><div class=\"acciones\">"
                + "<button type=\"submit\" name=\"accion\" value=\"cancelar\">Cancelar</button>"
                + "<button type=\"submit\" name=\"accion\" value=\"aceptar\" class=\"boton-principal\" autofocus>"
                + "Aceptar</button></div></form>";
        return pagina(peticion, "Cuestionario - Instrucciones", contenido);
    }

    /** Paso 4: pantalla del wizard posicionada en el bloque que aún no fue respondido. */
    private static RespuestaHttp bloque(Peticion peticion, Sesion sesion, Gestor_de_cuestionario gestor,
            Cuestionario cuestionario) {
        List<PreguntaBloqueDTO> preguntas = gestor.obtener_bloque_actual(cuestionario);
        if (preguntas.isEmpty()) {
            // todos los bloques fueron respondidos pero no se llegó a cerrar el cuestionario
            gestor.finalizar(cuestionario);
            sesion.put(COMPLETADO, Boolean.TRUE);
            return RespuestaHttp.redirigir("/cuestionario");
        }
        BloquePregunta actual = cuestionario.obtener_bloque_no_respondido();
        int total = cuestionario.getBloques().size();
        boolean ultimo = actual.getNumero() == total;
        boolean marcarFaltantes = Boolean.TRUE.equals(sesion.get(FALTAN));
        sesion.quitar(FALTAN);

        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"cabecera-cuestionario\"><span>Tiempo restante: <strong>")
                .append(tiempoRestante(cuestionario)).append("</strong></span></div>")
                .append(Html.pasos(PASOS, 1)).append(Html.errores(sesion.tomarMensajes()))
                .append("<form method=\"post\" action=\"/cuestionario\">")
                .append("<input type=\"hidden\" name=\"accion\" value=\"responder\">");
        boolean primera = true;
        for (PreguntaBloqueDTO pregunta : preguntas) {
            boolean falta = marcarFaltantes && pregunta.getId_detalle_elegido() == null;
            sb.append("<fieldset class=\"pregunta").append(falta ? " sin-responder" : "").append("\">")
                    .append("<legend>PREGUNTA ").append(pregunta.getNumero()).append("</legend>")
                    .append("<p class=\"enunciado\">").append(Html.e(pregunta.getEnunciado())).append("</p>")
                    .append("<div class=\"respuesta\">Respuesta:</div>");
            for (OpcionBloqueDTO opcion : pregunta.getOpciones()) {
                boolean elegida = pregunta.getId_detalle_elegido() != null
                        && pregunta.getId_detalle_elegido() == opcion.getId_detalle();
                sb.append("<label><input type=\"radio\" name=\"r_").append(pregunta.getId_respuesta())
                        .append("\" value=\"").append(opcion.getId_detalle()).append("\"")
                        .append(elegida ? " checked" : "");
                if (primera && (falta || !marcarFaltantes)) {
                    // con la ventana de faltantes abierta, el foco llega al cerrarla
                    sb.append(marcarFaltantes ? " data-foco" : " autofocus");
                    primera = false;
                }
                sb.append("> ").append(Html.e(opcion.getDescripcion())).append("</label>");
            }
            sb.append("</fieldset>");
        }
        sb.append("<p class=\"nota\">Una vez que avance al siguiente paso no podrá volver a ver ni modificar las"
                + " respuestas de este bloque.</p><div class=\"acciones\"><span class=\"nota\">Bloque ")
                .append(actual.getNumero()).append(" de ").append(total).append("</span>")
                .append("<button type=\"submit\" class=\"boton-principal\">").append(ultimo ? "Finalizar" : "Siguiente")
                .append("</button></div></form>");
        if (marcarFaltantes) {
            sb.append(ventanaFaltantes(preguntas));
        }
        return pagina(peticion, "Cuestionario", sb.toString());
    }

    /** Flujo 6.A: indica qué preguntas no fueron respondidas; las respondidas se conservan. */
    private static String ventanaFaltantes(List<PreguntaBloqueDTO> preguntas) {
        List<String> numeros = new ArrayList<>();
        StringBuilder tabla = new StringBuilder("<table class=\"grilla\"><thead><tr><th>N° Pregunta</th>")
                .append("<th>Estado</th></tr></thead><tbody>");
        for (PreguntaBloqueDTO pregunta : preguntas) {
            boolean respondida = pregunta.getId_detalle_elegido() != null;
            if (!respondida) {
                numeros.add(String.valueOf(pregunta.getNumero()));
            }
            tabla.append("<tr><td>").append(pregunta.getNumero()).append("</td><td>")
                    .append(respondida ? "Respondida" : "<span class=\"estado-sin-responder\">SIN RESPONDER</span>")
                    .append("</td></tr>");
        }
        tabla.append("</tbody></table>");
        String texto = "<p>Debe responder todas las preguntas del bloque. No han sido respondidas las preguntas: "
                + String.join(", ", numeros) + ".</p>" + tabla;
        return Html.emergente(Html.ADVERTENCIA, "Aviso", texto, Html.botonCerrar("Volver", true), null);
    }

    private static String tiempoRestante(Cuestionario cuestionario) {
        if (cuestionario.getFecha_ini() == null) {
            return "-";
        }
        long minutos = Math.round(cuestionario.getTiempoMax() * 60.0)
                - Duration.between(cuestionario.getFecha_ini(), LocalDateTime.now()).toMinutes();
        minutos = Math.max(0, minutos);
        return String.format(Locale.ROOT, "%02d:%02d", minutos / 60, minutos % 60);
    }

    /** Paso 8: mensaje emergente ME026.1; al aceptar, el caso de uso termina. */
    private static RespuestaHttp completado(Peticion peticion) {
        String contenido = Html.pasos(PASOS, 2)
                + Html.emergente(Html.EXITO, "Aviso", "<p>El cuestionario ha sido completado con éxito.</p>",
                        "<a class=\"boton boton-principal\" href=\"/salir\" autofocus>Aceptar</a>", "/salir");
        return pagina(peticion, "Cuestionario", contenido);
    }

    /**
     * Mensaje final (flujos 1.A, 1.B y máximo de accesos): el caso de uso termina y el candidato solo puede
     * salir.
     */
    private static RespuestaHttp cierre(Peticion peticion, String mensaje) {
        return pagina(peticion, "Cuestionario", "<div class=\"mensaje-ventana\">" + Html.icono(Html.INFO, 48)
                + "<p>" + Html.e(mensaje) + "</p></div><div class=\"acciones derecha\">"
                + "<a class=\"boton boton-principal\" href=\"/salir\" autofocus>Aceptar</a></div>");
    }
}
