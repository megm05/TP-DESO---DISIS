package com.grupo24.presentacion;

import com.grupo24.dominio.Consultor;
import com.grupo24.dominio.Evaluacion;
import com.grupo24.dto.OrdenMeritoCandidatoDTO;
import com.grupo24.dto.PuestoBusquedaDTO;
import com.grupo24.gestores.Gestor_de_orden_merito;
import com.grupo24.gestores.ValidacionException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pantalla del CU028 - Emitir Orden de mérito. */
public class PantallaOrdenMerito extends Pantalla {

    private static final String CRITERIOS = "orden.criterios";
    private static final String PUESTOS = "orden.puestos";

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        Consultor consultor = consultor(peticion);
        if (consultor == null) {
            return RespuestaHttp.redirigir("/login");
        }
        Sesion sesion = peticion.getSesion();
        Gestor_de_orden_merito gestor = new Gestor_de_orden_merito();
        if (!peticion.esPost()) {
            if ("1".equals(peticion.param("nueva"))) {
                sesion.quitar(CRITERIOS);
                sesion.quitar(PUESTOS);
            }
            return busqueda(peticion, sesion, Collections.emptyList());
        }
        String accion = peticion.param("accion");
        try {
            switch (accion) {
                case "cancelar":
                    // flujo 2.A: el caso de uso termina
                    sesion.quitar(CRITERIOS);
                    sesion.quitar(PUESTOS);
                    return RespuestaHttp.redirigir("/principal");
                case "buscar": {
                    // pasos 3 y 4
                    String[] criterios = {peticion.param("codigo"), peticion.param("nombre"), peticion.param("empresa")};
                    sesion.put(CRITERIOS, criterios);
                    sesion.quitar(PUESTOS);
                    Integer codigo = null;
                    if (!criterios[0].isEmpty()) {
                        codigo = peticion.paramEntero("codigo");
                        if (codigo == null) {
                            throw new ValidacionException("El Código debe ser numérico.");
                        }
                    }
                    sesion.put(PUESTOS, gestor.buscar_puestos(codigo, criterios[1], criterios[2]));
                    return busqueda(peticion, sesion, Collections.emptyList());
                }
                case "emitir": {
                    // pasos 5 y 6 (flujo 6.A si el puesto no posee evaluaciones)
                    Integer id_puesto = peticion.paramEntero("puesto");
                    if (id_puesto == null) {
                        throw new ValidacionException("Debe seleccionar un puesto de la lista.");
                    }
                    List<Evaluacion> evaluaciones = gestor.obtener_evaluaciones(id_puesto);
                    if (evaluaciones.isEmpty()) {
                        throw new ValidacionException("El puesto seleccionado no posee evaluaciones.");
                    }
                    return seleccionEvaluaciones(peticion, id_puesto, evaluaciones, Collections.emptyList());
                }
                case "generar": {
                    // paso 7
                    int id_puesto = peticion.paramEnteroOCero("puesto");
                    List<Integer> seleccionadas = peticion.paramsEnteros("evaluacion");
                    boolean todas = "1".equals(peticion.param("todas"));
                    if (!todas && seleccionadas.isEmpty()) {
                        return seleccionEvaluaciones(peticion, id_puesto, gestor.obtener_evaluaciones(id_puesto),
                                Collections.singletonList("Seleccione una o más evaluaciones, o la opción TODAS."));
                    }
                    List<Integer> ids = todas ? new ArrayList<>() : seleccionadas;
                    List<OrdenMeritoCandidatoDTO> listado = gestor.generar(id_puesto, ids, consultor);
                    return reporte(peticion, sesion, consultor, id_puesto, todas, ids, listado);
                }
                default:
                    return busqueda(peticion, sesion, Collections.emptyList());
            }
        } catch (ValidacionException e) {
            return busqueda(peticion, sesion, e.getErrores());
        }
    }

    /** Pasos 2 a 4: criterios de búsqueda y lista de puestos que coinciden. */
    private static RespuestaHttp busqueda(Peticion peticion, Sesion sesion, List<String> errores) {
        String[] criterios = sesion.get(CRITERIOS);
        if (criterios == null) {
            criterios = new String[] {"", "", ""};
        }
        List<PuestoBusquedaDTO> puestos = sesion.get(PUESTOS);
        // flujo 6.A (el puesto no posee evaluaciones): aviso en una ventana emergente
        boolean sinEvaluaciones = errores.size() == 1 && errores.get(0).contains("no posee evaluaciones");
        StringBuilder sb = new StringBuilder(sinEvaluaciones ? "" : Html.errores(errores));
        // Buscar queda primero en el formulario (Enter busca); Cancelar se muestra a su izquierda
        sb.append("<form method=\"post\" action=\"/orden-merito\"><fieldset class=\"recuadro\"><legend>Buscador</legend>")
                .append("<div class=\"filtros-fila\">")
                .append("<div class=\"campo\"><label for=\"codigo\">Código</label>")
                .append("<input type=\"text\" inputmode=\"numeric\" id=\"codigo\" name=\"codigo\" maxlength=\"9\" value=\"")
                .append(Html.e(criterios[0])).append("\"").append(sinEvaluaciones ? "" : " autofocus").append("></div>")
                .append("<div class=\"campo\"><label for=\"nombre\">Nombre del Puesto</label>")
                .append(Html.texto("nombre", criterios[1], 100, null)).append("</div>")
                .append("<div class=\"campo\"><label for=\"empresa\">Empresa</label>")
                .append(Html.texto("empresa", criterios[2], 100, null)).append("</div></div>")
                .append("<div class=\"acciones\"><div class=\"grupo\">")
                .append("<button type=\"submit\" name=\"accion\" value=\"buscar\" class=\"boton-principal\">")
                .append(Html.icono("lupa", 16)).append("Buscar</button>")
                .append("<button type=\"submit\" name=\"accion\" value=\"cancelar\" formnovalidate style=\"order:-1\">")
                .append("Cancelar</button></div><span class=\"nota\">Ingrese datos en al menos uno de los criterios de")
                .append(" búsqueda.</span></div></fieldset>");
        if (puestos != null) {
            sb.append("<h2 class=\"seccion\">Resultados</h2><table class=\"grilla\"><thead><tr><th>Código</th>")
                    .append("<th>Nombre del puesto</th><th>Empresa</th><th class=\"numero\">Candidatos</th>")
                    .append("<th class=\"numero\">Evaluaciones completas</th><th class=\"accion\">Orden de mérito</th>")
                    .append("</tr></thead><tbody>");
            if (puestos.isEmpty()) {
                sb.append("<tr><td colspan=\"6\" class=\"vacia\">No se encontraron puestos que coincidan con los"
                        + " criterios de búsqueda.</td></tr>");
            }
            for (PuestoBusquedaDTO puesto : puestos) {
                sb.append("<tr><td>").append(puesto.getCodigo()).append("</td><td>")
                        .append(Html.e(puesto.getNombre_puesto())).append("</td><td>")
                        .append(Html.e(puesto.getNombre_empresa())).append("</td><td class=\"numero\">")
                        .append(puesto.getCant_candidatos()).append("</td><td class=\"numero\">")
                        .append(puesto.getCant_evaluaciones_completas()).append("</td><td class=\"accion\">")
                        .append("<button type=\"submit\" class=\"boton-chico\" name=\"puesto\" value=\"")
                        .append(puesto.getCodigo()).append("\" formaction=\"/orden-merito?accion=emitir\"")
                        .append(" title=\"Emitir el orden de mérito\">").append(Html.icono("impresora", 16))
                        .append("Emitir</button></td></tr>");
            }
            sb.append("</tbody></table>");
        }
        sb.append("</form>");
        if (sinEvaluaciones) {
            sb.append(Html.emergente(Html.INFO, "Aviso", "<p>" + Html.e(errores.get(0)) + "</p>",
                    Html.botonCerrar("Aceptar", true), null));
        }
        return pagina(peticion, "Emitir Orden de mérito", sb.toString());
    }

    /** Paso 6: el actor elige para qué evaluación o evaluaciones se emite, o la opción TODAS. */
    private static RespuestaHttp seleccionEvaluaciones(Peticion peticion, int id_puesto, List<Evaluacion> evaluaciones,
            List<String> errores) {
        StringBuilder sb = new StringBuilder(Html.errores(errores));
        if (!evaluaciones.isEmpty()) {
            sb.append("<p class=\"introduccion\">Puesto: <strong>").append(Html.e(evaluaciones.get(0).getPuesto().getNombre()))
                    .append("</strong> - ").append(Html.e(evaluaciones.get(0).getPuesto().getEmpresa().getNombre()))
                    .append("</p>");
        }
        sb.append("<p>Seleccione para qué evaluación o evaluaciones desea emitir el orden de mérito:</p>")
                .append("<form method=\"post\" action=\"/orden-merito\">")
                .append("<input type=\"hidden\" name=\"puesto\" value=\"").append(id_puesto).append("\">")
                .append("<label class=\"casilla-todas\"><input type=\"checkbox\" name=\"todas\" value=\"1\"")
                .append(" data-todas=\"evaluacion\"> TODAS</label>")
                .append("<table class=\"grilla\"><thead><tr><th class=\"centro\"></th><th>Evaluación</th>")
                .append("<th>Fecha de generación</th><th>Consultor</th><th class=\"numero\">Candidatos</th></tr></thead><tbody>");
        for (Evaluacion evaluacion : evaluaciones) {
            sb.append("<tr class=\"seleccionable\"><td class=\"centro\"><input type=\"checkbox\" name=\"evaluacion\" value=\"")
                    .append(evaluacion.getId_evaluacion()).append("\" aria-label=\"Seleccionar\"></td><td>N° ")
                    .append(evaluacion.getId_evaluacion()).append("</td><td>")
                    .append(Html.fecha(evaluacion.getFecha_generacion())).append("</td><td>")
                    .append(Html.e(evaluacion.getConsultor().getNombre() + " " + evaluacion.getConsultor().getApellido()))
                    .append("</td><td class=\"numero\">").append(evaluacion.getCuestionarios().size()).append("</td></tr>");
        }
        sb.append("</tbody></table><div class=\"acciones\"><a class=\"boton\" href=\"/orden-merito\">Volver</a>")
                .append("<button type=\"submit\" name=\"accion\" value=\"generar\" class=\"boton-principal\">")
                .append(Html.icono("impresora", 16)).append("Emitir</button></div></form>");
        return pagina(peticion, "Emitir Orden de mérito", sb.toString());
    }

    /** Paso 7: listado de candidatos; pasos 8 y 9: impresión. */
    private static RespuestaHttp reporte(Peticion peticion, Sesion sesion, Consultor consultor, int id_puesto,
            boolean todas, List<Integer> ids, List<OrdenMeritoCandidatoDTO> listado) {
        PuestoBusquedaDTO puesto = null;
        List<PuestoBusquedaDTO> puestos = sesion.get(PUESTOS);
        if (puestos != null) {
            for (PuestoBusquedaDTO candidato : puestos) {
                if (candidato.getCodigo() == id_puesto) {
                    puesto = candidato;
                }
            }
        }
        List<OrdenMeritoCandidatoDTO> integran = new ArrayList<>();
        Map<String, List<OrdenMeritoCandidatoDTO>> resto = new LinkedHashMap<>();
        for (OrdenMeritoCandidatoDTO renglon : listado) {
            if (renglon.getPosicion() != null) {
                integran.add(renglon);
            } else {
                resto.computeIfAbsent(renglon.getEstado_cuestionario(), k -> new ArrayList<>()).add(renglon);
            }
        }

        StringBuilder sb = new StringBuilder("<div class=\"reporte\"><div class=\"reporte-encabezado\"><div>")
                .append("<p class=\"reporte-titulo\">ORDEN DE MÉRITOS</p><div class=\"reporte-datos\">");
        if (puesto != null) {
            sb.append("<span>Puesto: <strong>").append(Html.e(puesto.getNombre_puesto())).append("</strong> (código ")
                    .append(puesto.getCodigo()).append(")</span><span>Empresa: <strong>")
                    .append(Html.e(puesto.getNombre_empresa())).append("</strong></span>");
        }
        sb.append("<span>Evaluaciones: <strong>").append(todas ? "TODAS" : "N° " + joinIds(ids))
                .append("</strong></span></div></div><div class=\"reporte-fecha\">Fecha de emisión:<br><strong>")
                .append(Html.fechaHora(LocalDateTime.now())).append("</strong></div></div>");

        sb.append("<h2>Orden de mérito</h2><p class=\"nota\">Candidatos con el cuestionario completo que alcanzaron"
                + " las ponderaciones mínimas definidas para cada competencia del puesto, ordenados por puntaje obtenido.</p>");
        tabla(sb, integran, true, "Ningún candidato alcanzó las ponderaciones mínimas de todas las competencias.");

        String[][] grupos = {
            {"COMPLETO", "Cuestionarios completos que no alcanzaron las ponderaciones mínimas"},
            {"EN_PROCESO", "Cuestionarios en proceso"},
            {"ACTIVO", "Cuestionarios activos (todavía no iniciados)"},
            {"INCOMPLETO", "Cuestionarios incompletos"},
            {"SIN_CONTESTAR", "Cuestionarios sin contestar"},
        };
        for (String[] grupo : grupos) {
            List<OrdenMeritoCandidatoDTO> renglones = resto.get(grupo[0]);
            if (renglones != null) {
                sb.append("<div class=\"grupo-estado\"><h2>").append(Html.e(grupo[1])).append(" (")
                        .append(renglones.size()).append(")</h2>");
                tabla(sb, renglones, false, "");
                sb.append("</div>");
            }
        }
        sb.append("<p class=\"nota\">Puntaje obtenido: promedio de los totales de las competencias evaluadas (escala"
                + " de 0 a 10). Solo se informa para los cuestionarios completos.</p>")
                .append("<div class=\"reporte-pie\"><span>Emitido por: ")
                .append(Html.e(consultor.getNombre() + " " + consultor.getApellido())).append("</span>")
                .append("<span>Capit@l Humano - Gestión de Evaluaciones de Perfiles</span></div></div>")
                .append("<div class=\"acciones\"><div class=\"grupo\">")
                .append("<a class=\"boton\" href=\"/orden-merito\">Volver a la búsqueda</a>")
                .append("<a class=\"boton\" href=\"/principal\">Menú principal</a></div>")
                .append("<button type=\"button\" class=\"boton-principal\" data-imprimir>")
                .append(Html.icono("impresora", 16)).append("Imprimir</button></div>");
        return pagina(peticion, "Orden de mérito", sb.toString());
    }

    /**
     * Por cada candidato: tipo y número de documento, apellido, nombre, puntaje obtenido (solo si el
     * cuestionario está completo) y datos adicionales según el estado: completos → fecha de inicio, fecha de
     * fin y cantidad de accesos; el resto → fecha de inicio, último ingreso y cantidad de accesos
     * (observaciones 1 y 2).
     */
    private static void tabla(StringBuilder sb, List<OrdenMeritoCandidatoDTO> renglones, boolean conPosicion,
            String vacia) {
        sb.append("<table class=\"grilla\"><thead><tr>");
        if (conPosicion) {
            sb.append("<th class=\"centro\">Posición</th>");
        }
        sb.append("<th>Tipo doc.</th><th>Nro. documento</th><th>Apellido</th><th>Nombre</th>")
                .append("<th class=\"numero\">Puntaje obtenido</th><th>Estado</th><th>Datos adicionales</th></tr></thead><tbody>");
        if (renglones.isEmpty()) {
            sb.append("<tr><td colspan=\"").append(conPosicion ? 8 : 7).append("\" class=\"vacia\">")
                    .append(Html.e(vacia)).append("</td></tr>");
        }
        for (OrdenMeritoCandidatoDTO renglon : renglones) {
            boolean completo = "COMPLETO".equals(renglon.getEstado_cuestionario());
            sb.append("<tr>");
            if (conPosicion) {
                sb.append("<td class=\"centro\"><strong>").append(renglon.getPosicion()).append("</strong></td>");
            }
            sb.append("<td>").append(Html.e(renglon.getTipo_doc())).append("</td><td>")
                    .append(Html.e(renglon.getNro_doc())).append("</td><td>").append(Html.e(renglon.getApellido()))
                    .append("</td><td>").append(Html.e(renglon.getNombre())).append("</td><td class=\"numero\">")
                    .append(completo ? Html.numero(renglon.getPuntaje_obtenido()) : "")
                    .append("</td><td><span class=\"etiqueta ").append(Html.e(renglon.getEstado_cuestionario()))
                    .append("\">").append(Html.e(renglon.getEstado_cuestionario().replace('_', ' ')))
                    .append("</span></td><td class=\"nota\">Inicio: ").append(Html.fechaHora(renglon.getFecha_inicio()));
            if (completo) {
                sb.append("<br>Fin: ").append(Html.fechaHora(renglon.getFecha_fin()));
            } else {
                sb.append("<br>Último ingreso: ").append(Html.fechaHora(renglon.getUltimo_ingreso()));
            }
            sb.append("<br>Accesos: ").append(renglon.getCant_accesos()).append("</td></tr>");
        }
        sb.append("</tbody></table>");
    }

    private static String joinIds(List<Integer> ids) {
        List<String> textos = new ArrayList<>();
        for (Integer id : ids) {
            textos.add(String.valueOf(id));
        }
        return String.join(", ", textos);
    }
}
