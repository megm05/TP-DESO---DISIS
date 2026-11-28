package com.grupo24.presentacion;

import com.grupo24.dominio.Candidato;
import com.grupo24.dominio.Competencia;
import com.grupo24.dominio.Consultor;
import com.grupo24.dominio.Evaluacion;
import com.grupo24.dto.ArchivoDTO;
import com.grupo24.dto.CandidatoBusquedaDTO;
import com.grupo24.dto.CandidatoConClaveDTO;
import com.grupo24.dto.CompetenciaRequeridaDTO;
import com.grupo24.dto.PuestoSeleccionDTO;
import com.grupo24.gestores.Gestor_de_candidato;
import com.grupo24.gestores.Gestor_de_evaluacion;
import com.grupo24.gestores.Gestor_de_puesto;
import com.grupo24.gestores.ValidacionException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pantalla del CU025 - Evaluar a Candidatos. */
public class PantallaEvaluarCandidatos extends Pantalla {

    private static final String EVALUACION = "evaluar.evaluacion";
    private static final String PASO = "evaluar.paso";
    private static final String CRITERIOS = "evaluar.criterios";
    private static final String RESULTADOS = "evaluar.resultados";
    private static final String NO_EVALUABLES = "evaluar.no_evaluables";

    private static final String PASO_CANDIDATOS = "candidatos";
    private static final String PASO_PUESTO = "puesto";
    private static final String PASO_NO_EVALUABLE = "no_evaluable";
    private static final String PASO_CLAVES = "claves";
    private static final String PASO_EXPORTAR = "exportar";

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        Consultor consultor = consultor(peticion);
        if (consultor == null) {
            return RespuestaHttp.redirigir("/login");
        }
        Sesion sesion = peticion.getSesion();
        Gestor_de_evaluacion gestor = new Gestor_de_evaluacion();
        Evaluacion evaluacion = sesion.get(EVALUACION);

        if (peticion.getRuta().equals("/evaluar/fin") || peticion.getRuta().equals("/evaluar/cancelar")) {
            terminar(sesion);
            if (peticion.getRuta().equals("/evaluar/fin") && evaluacion != null && evaluacion.getId_evaluacion() > 0) {
                sesion.agregarMensaje("La evaluación N° " + evaluacion.getId_evaluacion()
                        + " se generó correctamente.");
            }
            return RespuestaHttp.redirigir("/principal");
        }
        if (peticion.getRuta().equals("/evaluar/excel")) {
            if (evaluacion == null) {
                return RespuestaHttp.redirigir("/principal");
            }
            // pasos 15 a 17: el navegador permite elegir la carpeta donde guardar el archivo
            ArchivoDTO archivo = gestor.exportar_claves_excel(evaluacion);
            return RespuestaHttp.descarga(archivo.getNombre_archivo(),
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", archivo.getContenido());
        }
        if (evaluacion == null || "1".equals(peticion.param("nueva"))) {
            // paso 1: el actor selecciona "Evaluar Candidatos" en el menú
            terminar(sesion);
            evaluacion = gestor.iniciar_evaluacion(consultor);
            sesion.put(EVALUACION, evaluacion);
            sesion.put(PASO, PASO_CANDIDATOS);
        }

        List<String> errores = Collections.emptyList();
        if (peticion.esPost()) {
            try {
                RespuestaHttp redireccion = procesar(peticion, sesion, gestor, evaluacion);
                if (redireccion != null) {
                    return redireccion;
                }
            } catch (ValidacionException e) {
                errores = e.getErrores();
            }
        }
        return mostrar(peticion, sesion, gestor, evaluacion, errores);
    }

    /** Procesa el botón presionado. */
    private RespuestaHttp procesar(Peticion peticion, Sesion sesion, Gestor_de_evaluacion gestor,
            Evaluacion evaluacion) {
        String accion = peticion.param("accion");
        if (!accionDelPaso(sesion.get(PASO), accion)) {
            // botón de un paso que ya pasó (por ejemplo, de una página vieja con Atrás): se ignora
            return null;
        }
        switch (accion) {
            case "buscar": {
                // pasos 2 y 3: búsqueda de candidatos
                String[] criterios = {peticion.param("apellido"), peticion.param("nombre"), peticion.param("numero")};
                sesion.put(CRITERIOS, criterios);
                Integer numero = null;
                if (!criterios[2].isEmpty()) {
                    numero = peticion.paramEntero("numero");
                    if (numero == null) {
                        sesion.put(RESULTADOS, new ArrayList<CandidatoBusquedaDTO>());
                        throw new ValidacionException("El Número de Candidato debe ser numérico.");
                    }
                }
                sesion.put(RESULTADOS, new Gestor_de_candidato().buscar(criterios[0], criterios[1], numero));
                return null;
            }
            case "agregar":
                // pasos 4 y 5 (flujo 4.A si no seleccionó candidatos)
                gestor.agregar_candidatos(evaluacion, peticion.paramsEnteros("sel"));
                return null;
            case "quitar":
                // flujo 5.A
                gestor.quitar_candidatos(evaluacion, peticion.paramsEnteros("quitar"));
                return null;
            case "siguiente_candidatos": {
                // paso 6 (flujo 6.A)
                if (evaluacion.getCandidatos().isEmpty()) {
                    throw new ValidacionException("Debe agregar uno o más candidatos a la grilla \"Candidatos a evaluar\".");
                }
                List<Candidato> noDisponibles = gestor.validar_candidatos_disponibles(evaluacion.getCandidatos());
                if (!noDisponibles.isEmpty()) {
                    List<String> errores = new ArrayList<>();
                    errores.add("Los siguientes candidatos poseen cuestionarios activos o en proceso y no pueden"
                            + " ser evaluados nuevamente:");
                    for (Candidato candidato : noDisponibles) {
                        errores.add(candidato.getApellido() + ", " + candidato.getNombre() + " (N° "
                                + candidato.getNum_candidato() + ")");
                    }
                    throw new ValidacionException(errores);
                }
                sesion.put(PASO, PASO_PUESTO);
                return null;
            }
            case "seleccionar_puesto": {
                // pasos 8 y 9 (flujo 9.A si alguna competencia no puede evaluarse)
                Integer id_puesto = peticion.paramEntero("puesto");
                if (id_puesto == null) {
                    throw new ValidacionException("Debe seleccionar una función.");
                }
                gestor.seleccionar_puesto(evaluacion, id_puesto);
                List<Competencia> noEvaluables = gestor.validar_competencias_evaluables(evaluacion.getPuesto());
                if (!noEvaluables.isEmpty()) {
                    List<String> nombres = new ArrayList<>();
                    for (Competencia competencia : noEvaluables) {
                        nombres.add(competencia.getNombre());
                    }
                    sesion.put(NO_EVALUABLES, nombres);
                    sesion.put(PASO, PASO_NO_EVALUABLE);
                }
                return null;
            }
            case "siguiente_puesto":
                // paso 10 (flujo 10.A si no seleccionó una función) y paso 11
                if (evaluacion.getPuesto() == null) {
                    throw new ValidacionException("Debe seleccionar una función.");
                }
                gestor.generar_evaluacion(evaluacion);
                sesion.put(PASO, PASO_CLAVES);
                return null;
            case "volver_candidatos":
                sesion.put(PASO, PASO_CANDIDATOS);
                return null;
            case "volver_puesto":
                sesion.put(PASO, PASO_PUESTO);
                return null;
            case "finalizar":
                // pasos 12 y 13: se guarda la evaluación con los cuestionarios en estado activo
                gestor.guardar_evaluacion(evaluacion);
                sesion.put(PASO, PASO_EXPORTAR);
                return null;
            default:
                return null;
        }
    }

    /** Acciones que corresponden a cada paso del asistente. */
    private static boolean accionDelPaso(String paso, String accion) {
        if (paso == null || accion == null) {
            return false;
        }
        switch (paso) {
            case PASO_CANDIDATOS:
                return accion.equals("buscar") || accion.equals("agregar") || accion.equals("quitar")
                        || accion.equals("siguiente_candidatos");
            case PASO_PUESTO:
                return accion.equals("seleccionar_puesto") || accion.equals("siguiente_puesto")
                        || accion.equals("volver_candidatos");
            case PASO_CLAVES:
                return accion.equals("volver_puesto") || accion.equals("finalizar");
            default:
                return false;
        }
    }

    private RespuestaHttp mostrar(Peticion peticion, Sesion sesion, Gestor_de_evaluacion gestor,
            Evaluacion evaluacion, List<String> errores) {
        String paso = sesion.get(PASO);
        StringBuilder sb = new StringBuilder();
        sb.append(indicadorDePasos(paso));
        String emergente = "";
        switch (paso) {
            case PASO_PUESTO:
                pasoPuesto(sb, gestor, evaluacion);
                break;
            case PASO_NO_EVALUABLE:
                pasoPuesto(sb, gestor, evaluacion);
                emergente = ventanaNoEvaluable(sesion);
                break;
            case PASO_CLAVES:
                pasoClaves(sb, gestor, evaluacion, true);
                break;
            case PASO_EXPORTAR:
                pasoClaves(sb, gestor, evaluacion, false);
                emergente = ventanaExportar(evaluacion);
                break;
            default:
                pasoCandidatos(sb, sesion, evaluacion);
        }
        // flujos 4.A, 6.A y 10.A: el mensaje se presenta en una ventana emergente
        sb.append(Html.emergenteErrores(errores, errores.size() > 1 ? "Aceptar" : "Volver")).append(emergente);
        String titulo = PASO_CANDIDATOS.equals(paso) ? "Evaluar Candidatos - Búsqueda de Candidatos"
                : "Evaluar Candidatos - Evaluación de Candidatos";
        return pagina(peticion, titulo, sb.toString());
    }

    private static String indicadorDePasos(String paso) {
        String[] claves = {PASO_CANDIDATOS, PASO_PUESTO, PASO_CLAVES, PASO_EXPORTAR};
        int actual = 0;
        for (int i = 0; i < claves.length; i++) {
            if (claves[i].equals(paso) || (PASO_NO_EVALUABLE.equals(paso) && i == 1)) {
                actual = i;
            }
        }
        return Html.pasos(new String[] {"Candidatos", "Función o puesto", "Claves de ingreso", "Exportar"}, actual);
    }

    /** Pasos 2 a 6: búsqueda, grilla de resultados y grilla "Candidatos a evaluar". */
    private static void pasoCandidatos(StringBuilder sb, Sesion sesion, Evaluacion evaluacion) {
        String[] criterios = sesion.get(CRITERIOS);
        if (criterios == null) {
            criterios = new String[] {"", "", ""};
        }
        List<CandidatoBusquedaDTO> resultados = sesion.get(RESULTADOS);
        sb.append("<form method=\"post\" action=\"/evaluar\"><fieldset class=\"recuadro\">")
                .append("<legend>Filtros de búsqueda</legend><div class=\"filtros\"><div class=\"filtros-campos\">")
                .append("<label for=\"apellido\">Apellido:</label>")
                .append(Html.texto("apellido", criterios[0], 100, "autofocus"))
                .append("<label for=\"nombre\">Nombre:</label>").append(Html.texto("nombre", criterios[1], 100, null))
                .append("<label for=\"numero\">Número de candidato:</label>")
                .append("<input type=\"text\" inputmode=\"numeric\" id=\"numero\" name=\"numero\" value=\"")
                .append(Html.e(criterios[2])).append("\" maxlength=\"9\"></div>")
                .append("<div><button type=\"submit\" name=\"accion\" value=\"buscar\" class=\"boton-grande\">")
                .append(Html.icono("lupa", 16)).append("Buscar</button>")
                .append("<p class=\"aviso-filtro\">").append(Html.icono(Html.ADVERTENCIA, 22))
                .append("<span>Si no completa ningún dato del filtro se mostrará la lista completa de candidatos.</span>")
                .append("</p></div></div></fieldset>");

        if (resultados != null) {
            sb.append("<h2 class=\"seccion\">Candidatos</h2>");
            grillaCandidatos(sb, resultados, true, "No se encontraron candidatos que cumplan con los criterios.");
            if (!resultados.isEmpty()) {
                sb.append("<div class=\"barra-grilla\"><button type=\"submit\" name=\"accion\" value=\"agregar\""
                        + " class=\"boton-principal\">Agregar</button></div>");
            }
        }

        List<CandidatoBusquedaDTO> aEvaluar = new ArrayList<>();
        for (Candidato candidato : evaluacion.getCandidatos()) {
            aEvaluar.add(new CandidatoBusquedaDTO(candidato.getNum_candidato(), candidato.getApellido(),
                    candidato.getNombre(), candidato.getTipo_doc().name(), candidato.getNro_doc()));
        }
        sb.append("<h2 class=\"seccion\">Candidatos a evaluar (").append(aEvaluar.size()).append(")</h2>");
        grillaCandidatos(sb, aEvaluar, false, "Todavía no agregó candidatos.");
        sb.append("<div class=\"acciones\"><a class=\"boton\" href=\"/evaluar/cancelar\">Cancelar</a>")
                .append("<button type=\"submit\" name=\"accion\" value=\"siguiente_candidatos\" class=\"boton-principal\">")
                .append("Siguiente</button></div></form>");
    }

    /** Grilla de candidatos. */
    private static void grillaCandidatos(StringBuilder sb, List<CandidatoBusquedaDTO> candidatos, boolean resultados,
            String vacia) {
        sb.append("<div class=\"desplazable\"><table class=\"grilla\"><thead><tr><th>N° Candidato</th>")
                .append("<th>Apellido</th><th>Nombre</th><th>Tipo doc.</th><th>Nro. documento</th><th class=\"accion\">")
                .append(resultados ? "Seleccionar" : "Acciones").append("</th></tr></thead><tbody>");
        if (candidatos.isEmpty()) {
            sb.append("<tr><td colspan=\"6\" class=\"vacia\">").append(Html.e(vacia)).append("</td></tr>");
        }
        for (CandidatoBusquedaDTO candidato : candidatos) {
            sb.append("<tr").append(resultados ? " class=\"seleccionable\"" : "").append("><td>")
                    .append(candidato.getNum_candidato()).append("</td><td>").append(Html.e(candidato.getApellido()))
                    .append("</td><td>").append(Html.e(candidato.getNombre())).append("</td><td>")
                    .append(Html.e(candidato.getTipo_doc())).append("</td><td>").append(Html.e(candidato.getNro_doc()))
                    .append("</td><td class=\"accion\">");
            if (resultados) {
                sb.append("<input type=\"checkbox\" name=\"sel\" value=\"").append(candidato.getNum_candidato())
                        .append("\" aria-label=\"Seleccionar ").append(Html.e(candidato.getApellido())).append("\">");
            } else {
                sb.append("<button type=\"submit\" class=\"boton-chico\" name=\"quitar\" value=\"")
                        .append(candidato.getNum_candidato()).append("\" formaction=\"/evaluar?accion=quitar\">")
                        .append(Html.icono("papelera", 15)).append("Quitar</button>");
            }
            sb.append("</td></tr>");
        }
        sb.append("</tbody></table></div>");
    }

    /** Pasos 8 a 10: selección de la función o puesto y competencias requeridas. */
    private static void pasoPuesto(StringBuilder sb, Gestor_de_evaluacion gestor, Evaluacion evaluacion) {
        int seleccionado = evaluacion.getPuesto() == null ? 0 : evaluacion.getPuesto().getId_puesto();
        sb.append("<p class=\"introduccion\">Candidatos a evaluar: <strong>").append(evaluacion.getCandidatos().size())
                .append("</strong>. Seleccione la función o puesto en el que desea evaluarlos.</p>")
                .append("<form method=\"post\" action=\"/evaluar\"><fieldset class=\"recuadro\">")
                .append("<legend>Función o puesto</legend><div class=\"desplazable\"><table class=\"grilla\"><thead><tr>")
                .append("<th class=\"centro\"></th><th>Código</th><th>Función o puesto</th><th>Empresa</th>")
                .append("</tr></thead><tbody>");
        for (PuestoSeleccionDTO puesto : new Gestor_de_puesto().buscar(null, null, null)) {
            sb.append("<tr class=\"seleccionable\"><td class=\"centro\"><input type=\"radio\" name=\"puesto\" value=\"")
                    .append(puesto.getId_puesto()).append("\" data-autoenviar=\"seleccionar_puesto\"")
                    .append(puesto.getId_puesto() == seleccionado ? " checked" : "")
                    .append(" aria-label=\"Seleccionar\"></td><td>").append(puesto.getId_puesto()).append("</td><td>")
                    .append(Html.e(puesto.getNombre_puesto())).append("</td><td>")
                    .append(Html.e(puesto.getNombre_empresa())).append("</td></tr>");
        }
        sb.append("</tbody></table></div></fieldset>");
        if (evaluacion.getPuesto() != null) {
            sb.append("<fieldset class=\"recuadro\"><legend>Competencias de la función ")
                    .append(Html.e(evaluacion.getPuesto().getNombre())).append("</legend><table class=\"grilla\"><thead>")
                    .append("<tr><th>Competencia</th><th class=\"numero\">Ponderación requerida</th></tr></thead><tbody>");
            for (CompetenciaRequeridaDTO competencia : gestor.obtener_competencias_requeridas(evaluacion.getPuesto())) {
                sb.append("<tr><td>").append(Html.e(competencia.getNombre_competencia()))
                        .append("</td><td class=\"numero\">").append(Html.numero(competencia.getPonderacion_requerida()))
                        .append("</td></tr>");
            }
            sb.append("</tbody></table></fieldset>");
        }
        sb.append("<div class=\"acciones\"><div class=\"grupo\">")
                .append("<a class=\"boton\" href=\"/evaluar/cancelar\">Cancelar</a>")
                .append("<button type=\"submit\" name=\"accion\" value=\"volver_candidatos\">Volver</button></div>")
                .append("<button type=\"submit\" name=\"accion\" value=\"siguiente_puesto\" class=\"boton-principal\">")
                .append("Siguiente</button></div></form>");
    }

    /** Flujo 9.A: competencias que no pueden ser evaluadas. */
    private static String ventanaNoEvaluable(Sesion sesion) {
        List<String> nombres = sesion.get(NO_EVALUABLES);
        StringBuilder texto = new StringBuilder("<p>La función seleccionada no puede ser evaluada porque las"
                + " siguientes competencias no poseen al menos un factor en condiciones de ser evaluado:</p><ul>");
        for (String nombre : nombres) {
            texto.append("<li>").append(Html.e(nombre)).append("</li>");
        }
        texto.append("</ul>");
        return Html.emergente(Html.ADVERTENCIA, "Aviso", texto.toString(),
                "<a class=\"boton boton-principal\" href=\"/evaluar/cancelar\" autofocus>Aceptar</a>", "/evaluar/cancelar");
    }

    /** Paso 11: candidatos a evaluar con su clave de ingreso. */
    private static void pasoClaves(StringBuilder sb, Gestor_de_evaluacion gestor, Evaluacion evaluacion,
            boolean conBotones) {
        sb.append("<p class=\"introduccion\">Función: <strong>").append(Html.e(evaluacion.getPuesto().getNombre()))
                .append("</strong> - ").append(Html.e(evaluacion.getPuesto().getEmpresa().getNombre())).append("</p>");
        grillaClaves(sb, gestor.obtener_candidatos_con_clave(evaluacion));
        if (conBotones) {
            sb.append("<p class=\"nota\">Al presionar Finalizar se guarda la evaluación y se crean los cuestionarios"
                    + " en estado activo.</p><form method=\"post\" action=\"/evaluar\"><div class=\"acciones\">")
                    .append("<div class=\"grupo\"><a class=\"boton\" href=\"/evaluar/cancelar\">Cancelar</a>")
                    .append("<button type=\"submit\" name=\"accion\" value=\"volver_puesto\">Volver</button></div>")
                    .append("<button type=\"submit\" name=\"accion\" value=\"finalizar\" class=\"boton-principal\"")
                    .append(" autofocus>Finalizar</button></div></form>");
        }
    }

    private static void grillaClaves(StringBuilder sb, List<CandidatoConClaveDTO> candidatos) {
        sb.append("<table class=\"grilla\"><thead><tr><th>Apellido</th><th>Nombre</th><th>Tipo doc.</th>")
                .append("<th>Nro. documento</th><th>Clave de ingreso</th></tr></thead><tbody>");
        for (CandidatoConClaveDTO candidato : candidatos) {
            sb.append("<tr><td>").append(Html.e(candidato.getApellido())).append("</td><td>")
                    .append(Html.e(candidato.getNombre())).append("</td><td>").append(Html.e(candidato.getTipo_doc()))
                    .append("</td><td>").append(Html.e(candidato.getNro_doc())).append("</td><td class=\"clave\">")
                    .append(Html.e(candidato.getClave_ingreso())).append("</td></tr>");
        }
        sb.append("</tbody></table>");
    }

    /** Pasos 13 a 17: evaluación guardada y exportación opcional de las claves a Excel. */
    private static String ventanaExportar(Evaluacion evaluacion) {
        String texto = "<p>La evaluación N° " + evaluacion.getId_evaluacion() + " fue guardada. Se crearon "
                + evaluacion.getCuestionarios().size() + " cuestionarios en estado activo.</p>"
                + "<p><strong>¿Desea exportar la lista de candidatos con sus claves a un archivo Excel?</strong></p>"
                + "<p class=\"nota\">Al elegir SI, el navegador descarga el archivo <em>aaaa-mm-dd-hh-mm-"
                + Html.e(evaluacion.getPuesto().getNombre()) + ".xlsx</em> y le permite elegir la carpeta donde"
                + " guardarlo. Luego presione NO o <a href=\"/evaluar/fin\">vuelva al menú principal</a>.</p>";
        String botones = Html.enlace("NO", "/evaluar/fin", "")
                + "<a class=\"boton boton-principal\" href=\"/evaluar/excel\" autofocus>SI</a>";
        return Html.emergente(Html.PREGUNTA, "Aviso", texto, botones, "/evaluar/fin");
    }

    private static void terminar(Sesion sesion) {
        sesion.quitar(EVALUACION);
        sesion.quitar(PASO);
        sesion.quitar(CRITERIOS);
        sesion.quitar(RESULTADOS);
        sesion.quitar(NO_EVALUABLES);
    }
}
