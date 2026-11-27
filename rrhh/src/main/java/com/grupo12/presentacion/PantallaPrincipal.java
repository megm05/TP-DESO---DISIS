package com.grupo12.presentacion;

import com.grupo12.dominio.Consultor;

/** Pantalla principal del consultor, con todas las opciones de su perfil (CU001 paso 6). */
public class PantallaPrincipal extends Pantalla {

    /** { título, caso de uso, ruta (vacía si no forma parte de esta entrega), descripción } */
    private static final String[][] OPCIONES = {
        {"Preguntas", "CU020", "/preguntas", "Buscar, dar de alta, modificar y dar de baja preguntas."},
        {"Evaluar Candidatos", "CU025", "/evaluar?nueva=1","Generar una evaluación y los cuestionarios de los candidatos."},
        {"Orden de Méritos", "CU028", "/orden-merito?nueva=1","Emitir el orden de mérito de un puesto."},
        {"Puestos", "CU008", "", "Gestión de puestos o funciones."},
        {"Competencias", "CU012", "", "Gestión de competencias."},
        {"Factores", "CU016", "", "Gestión de factores."},
        {"Opciones de Respuestas", "CU024", "", "Gestión de opciones de respuesta."},
        {"Importar Candidatos", "CU006", "", "Importación desde el sistema de Gestión de Candidatos y CV."},
        {"Exportar Resultados", "CU007", "", "Exportación de resultados a un archivo CSV."},
        {"Reporte Comparativo", "CU027", "", "Comparativo entre lo requerido y lo obtenido."},
    };

    @Override
    public RespuestaHttp atender(Peticion peticion) {
        Consultor consultor = consultor(peticion);
        if (consultor == null) {
            return RespuestaHttp.redirigir("/login");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<p class=\"introduccion\">Bienvenido/a, ").append(Html.e(consultor.getNombre()))
                .append(". Seleccione una opción:</p>");
        sb.append("<div class=\"menu\">");
        for (String[] opcion : OPCIONES) {
            if (opcion[2].isEmpty()) {
                sb.append("<div class=\"opcion-menu deshabilitada\" title=\"No forma parte de la implementación de esta entrega\">")
                        .append("<strong>").append(Html.e(opcion[0])).append("</strong><span>")
                        .append(Html.e(opcion[1])).append(" - ").append(Html.e(opcion[3]))
                        .append("</span></div>");
            } else {
                sb.append("<a class=\"opcion-menu\" href=\"").append(opcion[2]).append("\"><strong>")
                        .append(Html.e(opcion[0])).append("</strong><span>").append(Html.e(opcion[1]))
                        .append(" - ").append(Html.e(opcion[3])).append("</span></a>");
            }
        }
        sb.append("</div><p class=\"nota\">Las opciones en gris corresponden a casos de uso que no forman parte"
                + " de la implementación solicitada en el trabajo práctico.</p>");
        sb.append(Html.mensajesEmergentes(peticion.getSesion().tomarMensajes()));
        return pagina(peticion, "Menú principal", sb.toString());
    }
}
