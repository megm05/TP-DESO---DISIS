package com.grupo24.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Genera planillas Excel (formato .xlsx) sin librerías externas: un libro con una hoja, una fila de título,
 * una fila de encabezados y las filas de datos.
 */
public final class ExportadorExcel {

    private ExportadorExcel() {
    }

    public static byte[] generar(String nombreHoja, String titulo, String[] encabezados, List<String[]> filas) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            try (ZipOutputStream zip = new ZipOutputStream(salida)) {
                agregar(zip, "[Content_Types].xml", tiposDeContenido());
                agregar(zip, "_rels/.rels", relacionesRaiz());
                agregar(zip, "xl/workbook.xml", libro(nombreHoja));
                agregar(zip, "xl/_rels/workbook.xml.rels", relacionesLibro());
                agregar(zip, "xl/styles.xml", estilos());
                agregar(zip, "xl/worksheets/sheet1.xml", hoja(titulo, encabezados, filas));
            }
            return salida.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo generar el archivo Excel", e);
        }
    }

    private static void agregar(ZipOutputStream zip, String nombre, String contenido) throws IOException {
        zip.putNextEntry(new ZipEntry(nombre));
        zip.write(contenido.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static final String XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";
    private static final String NS = "http://schemas.openxmlformats.org/spreadsheetml/2006/main";
    private static final String NS_REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships";
    private static final String NS_PAQUETE = "http://schemas.openxmlformats.org/package/2006/relationships";

    private static String tiposDeContenido() {
        return XML + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
                + "<Default Extension=\"rels\""
                + " ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
                + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
                + "<Override PartName=\"/xl/workbook.xml\" ContentType="
                + "\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
                + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType="
                + "\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
                + "<Override PartName=\"/xl/styles.xml\" ContentType="
                + "\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
                + "</Types>";
    }

    private static String relacionesRaiz() {
        return XML + "<Relationships xmlns=\"" + NS_PAQUETE + "\">"
                + "<Relationship Id=\"rId1\" Type=\"" + NS_REL + "/officeDocument\" Target=\"xl/workbook.xml\"/>"
                + "</Relationships>";
    }

    private static String libro(String nombreHoja) {
        String nombre = nombreHoja.length() > 31 ? nombreHoja.substring(0, 31) : nombreHoja;
        return XML + "<workbook xmlns=\"" + NS + "\" xmlns:r=\"" + NS_REL + "\">"
                + "<sheets><sheet name=\"" + escapar(nombre) + "\" sheetId=\"1\" r:id=\"rId1\"/></sheets>"
                + "</workbook>";
    }

    private static String relacionesLibro() {
        return XML + "<Relationships xmlns=\"" + NS_PAQUETE + "\">"
                + "<Relationship Id=\"rId1\" Type=\"" + NS_REL + "/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
                + "<Relationship Id=\"rId2\" Type=\"" + NS_REL + "/styles\" Target=\"styles.xml\"/>"
                + "</Relationships>";
    }

    private static String estilos() {
        return XML + "<styleSheet xmlns=\"" + NS + "\">"
                + "<fonts count=\"2\">"
                + "<font><sz val=\"11\"/><name val=\"Calibri\"/></font>"
                + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font>"
                + "</fonts>"
                + "<fills count=\"2\">"
                + "<fill><patternFill patternType=\"none\"/></fill>"
                + "<fill><patternFill patternType=\"gray125\"/></fill>"
                + "</fills>"
                + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
                + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
                + "<cellXfs count=\"2\">"
                + "<xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
                + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/>"
                + "</cellXfs>"
                + "<cellStyles count=\"1\"><cellStyle name=\"Normal\" xfId=\"0\" builtinId=\"0\"/></cellStyles>"
                + "</styleSheet>";
    }

    private static String hoja(String titulo, String[] encabezados, List<String[]> filas) {
        StringBuilder sb = new StringBuilder(XML);
        sb.append("<worksheet xmlns=\"").append(NS).append("\">");
        sb.append("<cols>");
        for (int i = 1; i <= encabezados.length; i++) {
            sb.append("<col min=\"").append(i).append("\" max=\"").append(i)
                    .append("\" width=\"26\" customWidth=\"1\"/>");
        }
        sb.append("</cols><sheetData>");
        int numeroFila = 1;
        sb.append("<row r=\"").append(numeroFila).append("\">");
        celda(sb, 0, numeroFila, titulo, true);
        sb.append("</row>");
        numeroFila += 2;
        sb.append("<row r=\"").append(numeroFila).append("\">");
        for (int c = 0; c < encabezados.length; c++) {
            celda(sb, c, numeroFila, encabezados[c], true);
        }
        sb.append("</row>");
        for (String[] fila : filas) {
            numeroFila++;
            sb.append("<row r=\"").append(numeroFila).append("\">");
            for (int c = 0; c < fila.length; c++) {
                celda(sb, c, numeroFila, fila[c], false);
            }
            sb.append("</row>");
        }
        sb.append("</sheetData></worksheet>");
        return sb.toString();
    }

    private static void celda(StringBuilder sb, int columna, int fila, String valor, boolean negrita) {
        String referencia = String.valueOf((char) ('A' + columna)) + fila;
        sb.append("<c r=\"").append(referencia).append("\" t=\"inlineStr\"");
        if (negrita) {
            sb.append(" s=\"1\"");
        }
        sb.append("><is><t xml:space=\"preserve\">").append(escapar(valor == null ? "" : valor))
                .append("</t></is></c>");
    }

    private static String escapar(String texto) {
        return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}
