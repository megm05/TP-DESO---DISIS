package com.grupo12.presentacion;

import com.grupo12.dominio.Candidato;
import com.grupo12.dominio.Consultor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/** Utilidades para armar las páginas HTML. */
public final class Html {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static final String EXITO = "exito";
    public static final String ADVERTENCIA = "advertencia";
    public static final String INFO = "info";
    public static final String PREGUNTA = "pregunta";

    private Html() {
    }

    /** Escapa el texto para incluirlo en el HTML sin riesgo de inyección. */
    public static String e(Object valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.toString();
        StringBuilder sb = new StringBuilder(texto.length() + 16);
        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#39;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String fecha(LocalDate fecha) {
        return fecha == null ? "-" : fecha.format(FECHA);
    }

    public static String fechaHora(LocalDateTime fecha) {
        return fecha == null ? "-" : fecha.format(FECHA_HORA);
    }

    public static String numero(Double valor) {
        if (valor == null) {
            return "-";
        }
        String texto = String.format(Locale.ROOT, "%.2f", valor);
        if (texto.endsWith(".00")) {
            texto = texto.substring(0, texto.length() - 3);
        } else if (texto.endsWith("0")) {
            texto = texto.substring(0, texto.length() - 1);
        }
        return texto.replace('.', ',');
    }

    public static String errores(List<String> errores) {
        if (errores == null || errores.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder("<div class=\"panel panel-error\" role=\"alert\">");
        if (errores.size() == 1) {
            sb.append("<p>").append(e(errores.get(0))).append("</p>");
        } else {
            sb.append("<p><strong>Corrija los siguientes datos:</strong></p><ul>");
            for (String error : errores) {
                sb.append("<li>").append(e(error)).append("</li>");
            }
            sb.append("</ul>");
        }
        return sb.append("</div>").toString();
    }

    public static String aviso(String texto) {
        return texto == null ? "" : "<div class=\"panel panel-aviso\" role=\"status\"><p>" + e(texto) + "</p></div>";
    }

    public static String exito(String texto) {
        return texto == null ? "" : "<div class=\"panel panel-exito\" role=\"status\"><p>" + e(texto) + "</p></div>";
    }

    public static String mensajes(List<String> mensajes) {
        StringBuilder sb = new StringBuilder();
        for (String mensaje : mensajes) {
            sb.append(exito(mensaje));
        }
        return sb.toString();
    }

    public static String reemplazarHistorial(String direccion) {
        return "<script>history.replaceState(null, '', '" + direccion.replaceAll("[^A-Za-z0-9/?=&._-]", "")
                + "');</script>";
    }

    public static String errorCampo(String texto) {
        return texto == null ? "" : "<span class=\"error-campo\">" + e(texto) + "</span>";
    }

    public static String emergente(String tipo, String titulo, String textoHtml, String botonesHtml, String cerrar) {
        StringBuilder sb = new StringBuilder("<div class=\"emergente-fondo\" data-emergente>")
                .append("<div class=\"emergente\" role=\"dialog\" aria-modal=\"true\" aria-label=\"").append(e(titulo))
                .append("\"><div class=\"emergente-titulo\"><span>").append(e(titulo)).append("</span>");
        if (cerrar == null) {
            sb.append("<button type=\"button\" class=\"emergente-cerrar\" data-cerrar aria-label=\"Cerrar\">&times;</button>");
        } else if (!cerrar.isEmpty()) {
            sb.append("<a class=\"emergente-cerrar\" href=\"").append(e(cerrar)).append("\" aria-label=\"Cerrar\">&times;</a>");
        }
        sb.append("</div><div class=\"emergente-cuerpo\">");
        if (tipo != null) {
            sb.append("<div class=\"icono\">").append(icono(tipo, 54)).append("</div>");
        }
        sb.append("<div class=\"emergente-texto\">").append(textoHtml).append("</div></div>");
        if (botonesHtml != null && !botonesHtml.isEmpty()) {
            sb.append("<div class=\"emergente-botones\">").append(botonesHtml).append("</div>");
        }
        return sb.append("</div></div>").toString();
    }

    public static String botonCerrar(String texto, boolean principal) {
        return "<button type=\"button\" class=\"" + (principal ? "boton-principal" : "") + "\" data-cerrar"
                + (principal ? " autofocus" : "") + ">" + e(texto) + "</button>";
    }

    public static String enlace(String texto, String destino, String clase) {
        return "<a class=\"boton" + (clase == null || clase.isEmpty() ? "" : " " + clase) + "\" href=\""
                + e(destino) + "\">" + e(texto) + "</a>";
    }

    public static String emergenteErrores(List<String> errores, String textoBoton) {
        if (errores == null || errores.isEmpty()) {
            return "";
        }
        StringBuilder texto = new StringBuilder("<p>").append(e(errores.get(0))).append("</p>");
        if (errores.size() > 1) {
            texto.append("<ul>");
            for (String detalle : errores.subList(1, errores.size())) {
                texto.append("<li>").append(e(detalle)).append("</li>");
            }
            texto.append("</ul>");
        }
        return emergente(ADVERTENCIA, "Aviso", texto.toString(), botonCerrar(textoBoton, true), null);
    }

    public static String mensajesEmergentes(List<String> mensajes) {
        if (mensajes == null || mensajes.isEmpty()) {
            return "";
        }
        boolean exito = false;
        StringBuilder texto = new StringBuilder();
        for (String mensaje : mensajes) {
            String minusculas = mensaje.toLowerCase(Locale.ROOT);
            exito |= minusculas.contains("éxito") || minusculas.contains("correctamente");
            texto.append("<p>").append(e(mensaje)).append("</p>");
        }
        return emergente(exito ? EXITO : INFO, "Aviso", texto.toString(), botonCerrar("Aceptar", true), null);
    }

    /** Barra de pasos: círculos numerados unidos por una línea; actual empieza en 0. */
    public static String pasos(String[] nombres, int actual) {
        StringBuilder sb = new StringBuilder("<div class=\"pasos\" aria-label=\"Pasos\">");
        for (int i = 0; i < nombres.length; i++) {
            if (i > 0) {
                sb.append("<div class=\"linea\"></div>");
            }
            String clase = i < actual ? " hecho" : i == actual ? " actual" : "";
            sb.append("<div class=\"paso").append(clase).append("\"").append(i == actual ? " aria-current=\"step\"" : "")
                    .append("><span class=\"circulo\">").append(i + 1).append("</span><span>").append(e(nombres[i]))
                    .append("</span></div>");
        }
        return sb.append("</div>").toString();
    }

    public static String paginacion(int pagina, int totalPaginas, String base) {
        if (totalPaginas <= 1) {
            return "";
        }
        String separador = base.contains("?") ? "&" : "?";
        StringBuilder sb = new StringBuilder("<nav class=\"paginacion\" aria-label=\"Páginas\">");
        int anterior = 0;
        for (int i = 1; i <= totalPaginas; i++) {
            boolean mostrar = totalPaginas <= 7 || i == 1 || i == totalPaginas || Math.abs(i - pagina) <= 1;
            if (!mostrar) {
                continue;
            }
            if (anterior > 0 && i - anterior > 1) {
                sb.append("<span class=\"salto\">&hellip;</span>");
            }
            if (i == pagina) {
                sb.append("<span class=\"actual\" aria-current=\"page\">").append(i).append("</span>");
            } else {
                sb.append("<a href=\"").append(e(base + separador + "pag=" + i)).append("\">").append(i).append("</a>");
            }
            anterior = i;
        }
        return sb.append("</nav>").toString();
    }

    public static String icono(String nombre, int tamanio) {
        String svg;
        switch (nombre) {
            case EXITO:
                svg = "<circle cx=\"24\" cy=\"24\" r=\"22\" fill=\"#2e7d32\"/><path d=\"M14 24.5l7 7 13-14\" fill=\"none\""
                        + " stroke=\"#fff\" stroke-width=\"4.5\" stroke-linecap=\"round\" stroke-linejoin=\"round\"/>";
                break;
            case ADVERTENCIA:
                svg = "<path d=\"M24 4L46 43H2z\" fill=\"#e8940c\" stroke=\"#e8940c\" stroke-width=\"2\""
                        + " stroke-linejoin=\"round\"/><rect x=\"21.6\" y=\"16\" width=\"4.8\" height=\"15\" rx=\"2.2\""
                        + " fill=\"#fff\"/><circle cx=\"24\" cy=\"36.5\" r=\"2.8\" fill=\"#fff\"/>";
                break;
            case PREGUNTA:
                svg = "<circle cx=\"24\" cy=\"24\" r=\"21\" fill=\"none\" stroke=\"#e8940c\" stroke-width=\"3\"/>"
                        + "<path d=\"M18 18.5a6 6 0 1 1 8.5 5.4c-1.6.8-2.5 2-2.5 3.6v1.8\" fill=\"none\" stroke=\"#e8940c\""
                        + " stroke-width=\"3.4\" stroke-linecap=\"round\"/><circle cx=\"24\" cy=\"35\" r=\"2.4\" fill=\"#e8940c\"/>";
                break;
            case "lapiz":
                return "<svg viewBox=\"0 0 24 24\" width=\"" + tamanio + "\" height=\"" + tamanio + "\" aria-hidden=\"true\">"
                        + "<path d=\"M4 20h4L19 9l-4-4L4 16v4z\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\""
                        + " stroke-linejoin=\"round\"/><path d=\"M13.5 6.5l4 4\" stroke=\"currentColor\" stroke-width=\"2\"/></svg>";
            case "impresora":
                return "<svg viewBox=\"0 0 24 24\" width=\"" + tamanio + "\" height=\"" + tamanio + "\" aria-hidden=\"true\">"
                        + "<path d=\"M7 9V3h10v6\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"/><rect x=\"3\""
                        + " y=\"9\" width=\"18\" height=\"8\" rx=\"1.5\" fill=\"none\" stroke=\"currentColor\""
                        + " stroke-width=\"2\"/><path d=\"M7 14h10v7H7z\" fill=\"#fff\" stroke=\"currentColor\""
                        + " stroke-width=\"2\"/></svg>";
            case "papelera":
                return "<svg viewBox=\"0 0 24 24\" width=\"" + tamanio + "\" height=\"" + tamanio + "\" aria-hidden=\"true\">"
                        + "<path d=\"M4 7h16M9 7V4h6v3M6 7l1 13h10l1-13\" fill=\"none\" stroke=\"currentColor\""
                        + " stroke-width=\"2\" stroke-linejoin=\"round\"/></svg>";
            case "lupa":
                return "<svg viewBox=\"0 0 24 24\" width=\"" + tamanio + "\" height=\"" + tamanio + "\" aria-hidden=\"true\">"
                        + "<circle cx=\"10.5\" cy=\"10.5\" r=\"6.5\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"/>"
                        + "<path d=\"M15.5 15.5L21 21\" stroke=\"currentColor\" stroke-width=\"2.2\" stroke-linecap=\"round\"/></svg>";
            default:
                svg = "<circle cx=\"24\" cy=\"24\" r=\"21\" fill=\"none\" stroke=\"#2f5fb3\" stroke-width=\"3\"/><circle"
                        + " cx=\"24\" cy=\"14.5\" r=\"2.8\" fill=\"#2f5fb3\"/><rect x=\"21.8\" y=\"20\" width=\"4.4\""
                        + " height=\"16\" rx=\"2\" fill=\"#2f5fb3\"/>";
        }
        return "<svg viewBox=\"0 0 48 48\" width=\"" + tamanio + "\" height=\"" + tamanio + "\" aria-hidden=\"true\">"
                + svg + "</svg>";
    }

    public static String pagina(String titulo, Sesion sesion, String contenido) {
        return pagina(titulo, sesion, contenido, "");
    }

    public static String pagina(String titulo, Sesion sesion, String contenido, String claseVentana) {
        StringBuilder sb = new StringBuilder(contenido.length() + 2048);
        sb.append("<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
                .append("<title>").append(e(titulo)).append(" - Capit@l Humano</title>")
                .append("<link rel=\"stylesheet\" href=\"/estaticos/estilos.css\">")
                .append("<script src=\"/estaticos/app.js\" defer></script>")
                .append("</head><body><header class=\"barra\"><div class=\"marca\">")
                .append("<span class=\"logo\">Capit@l Humano</span>")
                .append("<span class=\"subtitulo\">Gestión de Evaluaciones de Perfiles</span></div>");
        if (sesion != null) {
            Consultor consultor = sesion.get(Sesion.CONSULTOR);
            Candidato candidato = sesion.get(Sesion.CANDIDATO);
            String usuario = null;
            if (consultor != null) {
                usuario = "Consultor: " + consultor.getNombre() + " " + consultor.getApellido();
            } else if (candidato != null) {
                usuario = "Candidato: " + candidato.getApellido() + ", " + candidato.getNombre();
            }
            if (usuario != null) {
                sb.append("<div class=\"usuario\"><span>").append(e(usuario)).append("</span>");
                if (consultor != null) {
                    sb.append("<a class=\"boton boton-claro\" href=\"/principal\">Menú principal</a>");
                }
                sb.append("<a class=\"boton boton-claro\" href=\"/salir\">Salir</a></div>");
            }
        }
        sb.append("</header><main class=\"ventana").append(claseVentana.isEmpty() ? "" : " " + claseVentana)
                .append("\"><div class=\"ventana-titulo\"><h1>").append(e(titulo)).append("</h1></div>")
                .append("<div class=\"ventana-cuerpo\">").append(contenido).append("</div>")
                .append("</main><footer class=\"pie\">Diseño de Sistemas 2026 - UTN FRSF - Grupo 12</footer>")
                .append("</body></html>");
        return sb.toString();
    }

    /** Campo de texto que se ingresa SIEMPRE en mayúsculas. */
    public static String texto(String nombre, String valor, int maximo, String extra) {
        return "<input type=\"text\" class=\"mayusculas\" id=\"" + nombre + "\" name=\"" + nombre + "\" value=\""
                + e(valor) + "\"" + (maximo > 0 ? " maxlength=\"" + maximo + "\"" : "")
                + (extra == null ? "" : " " + extra) + ">";
    }
}
