package app.utils;

import app.logica.PedidoDAO;
import app.modelo.Pedido;

import java.io.*;
import java.util.List;

/**
 * ProcessManager — Exportación de pedidos a PDF usando ProcessBuilder.
 *
 * Genera un PDF real sin librerías externas escribiendo directamente
 * la estructura mínima del formato PDF 1.4. Usa ProcessBuilder para
 * abrir el PDF generado con el visor predeterminado del sistema,
 * cumpliendo el requisito técnico del módulo de procesos.
 *
 * @author EatMe Team
 */
public class ProcessManager {

    /**
     * Exporta todos los pedidos a un archivo PDF en el directorio de ejecución.
     * Usa ProcessBuilder para abrir el PDF con el visor del sistema operativo.
     *
     * @return Ruta del archivo PDF generado
     */
    public static String exportarPedidosPDF() {
        PedidoDAO pedidoDAO = new PedidoDAO();
        List<Pedido> pedidos = pedidoDAO.obtenerTodos();

        String ruta = "pedidos_exportados.pdf";

        try {
            // Construimos el contenido de texto del PDF
            StringBuilder contenido = new StringBuilder();
            contenido.append("PEDIDOS EXPORTADOS - EatMe\n\n");
            contenido.append("--------------------------------------------------\n");
            for (Pedido p : pedidos) {
                contenido.append("Pedido #").append(p.getId_pedido())
                         .append("  |  Usuario: ").append(p.getId_usuario())
                         .append("  |  Producto: ").append(p.getNombreProducto())
                         .append("  |  Cantidad: ").append(p.getCantidad())
                         .append("  |  Fecha: ").append(p.getFecha())
                         .append("\n");
            }
            contenido.append("--------------------------------------------------\n");
            contenido.append("Total de pedidos: ").append(pedidos.size());

            // Generamos el PDF mínimo válido sin librerías externas
            escribirPdfMinimo(ruta, "Pedidos EatMe", contenido.toString());

            System.out.println("PDF exportado correctamente: " + ruta);

            // ProcessBuilder: abre el PDF con el visor predeterminado del SO
            String os = System.getProperty("os.name").toLowerCase();
            String[] cmd;
            if (os.contains("win")) {
                cmd = new String[]{"cmd", "/c", "start", "", ruta};
            } else if (os.contains("mac")) {
                cmd = new String[]{"open", ruta};
            } else {
                cmd = new String[]{"xdg-open", ruta};
            }
            new ProcessBuilder(cmd).start();

        } catch (IOException e) {
            System.out.println("Error exportando PDF: " + e.getMessage());
        }

        return ruta;
    }

    /**
     * Escribe un PDF 1.4 mínimo válido con el título y el texto dados.
     * No requiere ninguna librería externa: solo Java SE estándar.
     * El texto se divide en líneas de 80 caracteres para que quepa en la página.
     *
     * @param ruta    Ruta del archivo de salida
     * @param titulo  Título del documento
     * @param texto   Contenido de texto a incluir
     * @throws IOException si hay error al escribir el archivo
     */
    private static void escribirPdfMinimo(String ruta, String titulo, String texto) throws IOException {
        // Dividimos el texto en líneas para el stream de contenido PDF
        String[] lineas = texto.split("\n");
        StringBuilder stream = new StringBuilder();
        stream.append("BT\n");
        stream.append("/F1 10 Tf\n");
        stream.append("50 770 Td\n");
        stream.append("14 TL\n"); // interlineado

        for (String linea : lineas) {
            // Escapamos caracteres especiales PDF
            String escapada = linea
                .replace("\\", "\\\\")
                .replace("(", "\\(")
                .replace(")", "\\)")
                .replace("\r", "");
            // Limitamos longitud para que no salga de la página
            if (escapada.length() > 100) escapada = escapada.substring(0, 100) + "...";
            stream.append("(").append(escapada).append(") '\n");
        }
        stream.append("ET\n");

        byte[] streamBytes = stream.toString().getBytes("ISO-8859-1");

        // Cabeceras de objetos PDF
        String header  = "%PDF-1.4\n";
        String obj1    = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
        String obj2    = "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n";
        String obj3    = "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842]\n"
                       + "   /Contents 4 0 R /Resources << /Font << /F1 5 0 R >> >> >>\nendobj\n";
        String obj4    = "4 0 obj\n<< /Length " + streamBytes.length + " >>\nstream\n";
        String obj4end = "\nendstream\nendobj\n";
        String obj5    = "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>\nendobj\n";

        // Calculamos offsets para la tabla xref
        int off1 = header.length();
        int off2 = off1 + obj1.length();
        int off3 = off2 + obj2.length();
        int off4 = off3 + obj3.length();
        int off5 = off4 + obj4.length() + streamBytes.length + obj4end.length();

        String xref = "xref\n0 6\n0000000000 65535 f \n"
            + String.format("%010d", off1) + " 00000 n \n"
            + String.format("%010d", off2) + " 00000 n \n"
            + String.format("%010d", off3) + " 00000 n \n"
            + String.format("%010d", off4) + " 00000 n \n"
            + String.format("%010d", off5) + " 00000 n \n";

        int startxref = off5 + obj5.length();
        String trailer = "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + startxref + "\n%%EOF\n";

        // Escribimos todo el PDF en binario
        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            fos.write(header.getBytes("ISO-8859-1"));
            fos.write(obj1.getBytes("ISO-8859-1"));
            fos.write(obj2.getBytes("ISO-8859-1"));
            fos.write(obj3.getBytes("ISO-8859-1"));
            fos.write(obj4.getBytes("ISO-8859-1"));
            fos.write(streamBytes);
            fos.write(obj4end.getBytes("ISO-8859-1"));
            fos.write(obj5.getBytes("ISO-8859-1"));
            fos.write(xref.getBytes("ISO-8859-1"));
            fos.write(trailer.getBytes("ISO-8859-1"));
        }
    }
}
