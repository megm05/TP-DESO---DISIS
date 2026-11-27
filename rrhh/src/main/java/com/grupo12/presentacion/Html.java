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

    /** Número con hasta dos decimales y coma decimal. */
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

    /** Panel con la lista de errores, ubicado arriba del formulario para no tapar campos ni botones. */
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

    /**
     * Página completa con el encabezado del sistema.
     *
     * @param titulo    título de la pantalla
     * @param sesion    sesión actual (para mostrar el usuario); puede ser null
     * @param contenido HTML del cuerpo de la pantalla
     */
    public static String pagina(String titulo, Sesion sesion, String contenido) {
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
        sb.append("</header><main class=\"contenido\"><h1>").append(e(titulo)).append("</h1>")
                .append(contenido)
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
