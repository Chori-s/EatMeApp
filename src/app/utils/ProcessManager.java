package app.utils;

import app.logica.PedidoDAO;
import app.modelo.Pedido;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProcessManager — Exportación de pedidos a PDF usando ProcessBuilder.
 *
 * Genera un PDF real sin librerías externas escribiendo directamente la
 * estructura mínima del formato PDF 1.4. Usa ProcessBuilder para abrir el PDF
 * generado con el visor predeterminado del sistema, cumpliendo el requisito
 * técnico del módulo de procesos.
 *
 * @author EatMe Team
 */
public class ProcessManager {

	/**
	 * Exporta todos los pedidos a un archivo PDF en el directorio de ejecución. Usa
	 * ProcessBuilder para abrir el PDF con el visor del sistema operativo.
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
			for (Pedido p : pedidos) {

				contenido.append("Pedido #").append(p.getId_pedido()).append(" | Usuario: ").append(p.getId_usuario())
						.append("\n");

				contenido.append("Producto: ").append(p.getNombreProducto()).append("\n");

				contenido.append("Cantidad: ").append(p.getCantidad()).append(" | Fecha: ").append(p.getFecha())
						.append("\n");

				contenido.append("---------------------------------------------\n");
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
				cmd = new String[] { "cmd", "/c", "start", "", ruta };
			} else if (os.contains("mac")) {
				cmd = new String[] { "open", ruta };
			} else {
				cmd = new String[] { "xdg-open", ruta };
			}
			new ProcessBuilder(cmd).start();

		} catch (IOException e) {
			System.out.println("Error exportando PDF: " + e.getMessage());
		}

		return ruta;
	}

	/**
	 * Escribe un PDF 1.4 mínimo válido con el título y el texto dados. No requiere
	 * ninguna librería externa: solo Java SE estándar. El texto se divide en líneas
	 * de 80 caracteres para que quepa en la página.
	 *
	 * @param ruta   Ruta del archivo de salida
	 * @param titulo Título del documento
	 * @param texto  Contenido de texto a incluir
	 * @throws IOException si hay error al escribir el archivo
	 */
	private static void escribirPdfMinimo(String ruta, String titulo, String texto) throws IOException {
	    String[] lineas = texto.split("\n");

	    // Configuración de página
	    final int PAGE_HEIGHT = 842;
	    final int PAGE_WIDTH = 595;
	    final int MARGIN_TOP = 800;
	    final int MARGIN_BOTTOM = 40;
	    final int LINE_HEIGHT = 16;
	    final int HEADER_SPACE = 60; // espacio que ocupa el título + subtítulo + separador

	    // Dividir líneas en páginas
	    int linesPerPage = (MARGIN_TOP - MARGIN_BOTTOM - HEADER_SPACE) / LINE_HEIGHT;
	    List<List<String>> paginas = new ArrayList<>();
	    List<String> paginaActual = new ArrayList<>();

	    for (String linea : lineas) {
	        paginaActual.add(linea);
	        if (paginaActual.size() >= linesPerPage) {
	            paginas.add(new ArrayList<>(paginaActual));
	            paginaActual.clear();
	        }
	    }
	    if (!paginaActual.isEmpty()) paginas.add(paginaActual);

	    int numPaginas = paginas.size();

	    // Construir streams de contenido por página
	    List<byte[]> streamsBytes = new ArrayList<>();
	    for (int i = 0; i < numPaginas; i++) {
	        StringBuilder stream = new StringBuilder();
	        stream.append("BT\n");
	        stream.append("/F1 18 Tf\n");
	        stream.append("30 810 Td\n");
	        stream.append("(").append(titulo).append(") Tj\n");
	        stream.append("/F1 12 Tf\n");
	        stream.append("0 -20 Td\n");
	        stream.append("(Generado por EatMe App) Tj\n");
	        stream.append("0 -15 Td\n");
	        stream.append("(------------------------------------------------------------) Tj\n");
	        stream.append("0 -20 Td\n");

	        for (String linea : paginas.get(i)) {
	            String escapada = linea
	                .replace("\\", "\\\\")
	                .replace("(", "\\(")
	                .replace(")", "\\)")
	                .replace("\r", "");
	            stream.append("(").append(escapada).append(") Tj\n");
	            stream.append("0 -").append(LINE_HEIGHT).append(" Td\n");
	        }

	        // Número de página al pie
	        stream.append("ET\n");
	        stream.append("BT\n");
	        stream.append("/F1 7 Tf\n");
	        stream.append("250 20 Td\n");
	        stream.append("(Pagina ").append(i + 1).append(" de ").append(numPaginas).append(") Tj\n");
	        stream.append("ET\n");

	        streamsBytes.add(stream.toString().getBytes("ISO-8859-1"));
	    }

	    // Calcular número de objetos PDF:
	    // obj 1 = Catalog, obj 2 = Pages, obj 3..N+2 = Page objects,
	    // obj N+3..2N+2 = Content streams, obj 2N+3 = Font
	    int totalObjs = 3 + 2 * numPaginas; // catalog + pages + pages_obj*n + streams*n + font

	    // Construcción del PDF
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();

	    String header = "%PDF-1.4\n";
	    baos.write(header.getBytes("ISO-8859-1"));

	    List<Integer> offsets = new ArrayList<>();
	    // obj 1 = Catalog (referencias obj 2)
	    offsets.add(baos.size());
	    String obj1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n";
	    baos.write(obj1.getBytes("ISO-8859-1"));

	    // obj 2 = Pages
	    offsets.add(baos.size());
	    StringBuilder kidsRef = new StringBuilder("[");
	    for (int i = 0; i < numPaginas; i++) {
	        kidsRef.append((3 + i)).append(" 0 R ");
	    }
	    kidsRef.append("]");
	    String obj2 = "2 0 obj\n<< /Type /Pages /Kids " + kidsRef + " /Count " + numPaginas + " >>\nendobj\n";
	    baos.write(obj2.getBytes("ISO-8859-1"));

	    // Font object number
	    int fontObjNum = 3 + numPaginas + numPaginas; // después de páginas y streams

	    // obj 3..N+2 = Page objects
	    for (int i = 0; i < numPaginas; i++) {
	        offsets.add(baos.size());
	        int pageObjNum = 3 + i;
	        int contentObjNum = 3 + numPaginas + i;
	        String objPage = pageObjNum + " 0 obj\n"
	            + "<< /Type /Page\n"
	            + "/Parent 2 0 R\n"
	            + "/MediaBox [0 0 " + PAGE_WIDTH + " " + PAGE_HEIGHT + "]\n"
	            + "/Contents " + contentObjNum + " 0 R\n"
	            + "/Resources << /Font << /F1 " + fontObjNum + " 0 R >> >>\n"
	            + ">>\nendobj\n";
	        baos.write(objPage.getBytes("ISO-8859-1"));
	    }

	    // obj N+3..2N+2 = Content streams
	    for (int i = 0; i < numPaginas; i++) {
	        offsets.add(baos.size());
	        int contentObjNum = 3 + numPaginas + i;
	        byte[] sb = streamsBytes.get(i);
	        String streamHeader = contentObjNum + " 0 obj\n<< /Length " + sb.length + " >>\nstream\n";
	        baos.write(streamHeader.getBytes("ISO-8859-1"));
	        baos.write(sb);
	        baos.write("\nendstream\nendobj\n".getBytes("ISO-8859-1"));
	    }

	    // Font object
	    offsets.add(baos.size());
	    String objFont = fontObjNum + " 0 obj\n"
	        + "<< /Type /Font\n/Subtype /Type1\n/BaseFont /Helvetica >>\nendobj\n";
	    baos.write(objFont.getBytes("ISO-8859-1"));

	    // xref
	    int xrefOffset = baos.size();
	    int totalXrefObjs = 1 + totalObjs; // +1 por el objeto 0 libre
	    StringBuilder xref = new StringBuilder();
	    xref.append("xref\n0 ").append(totalXrefObjs).append("\n");
	    xref.append("0000000000 65535 f \n");
	    for (int off : offsets) {
	        xref.append(String.format("%010d", off)).append(" 00000 n \n");
	    }
	    baos.write(xref.toString().getBytes("ISO-8859-1"));

	    String trailer = "trailer\n<< /Size " + totalXrefObjs + " /Root 1 0 R >>\nstartxref\n" + xrefOffset + "\n%%EOF\n";
	    baos.write(trailer.getBytes("ISO-8859-1"));

	    try (FileOutputStream fos = new FileOutputStream(ruta)) {
	        fos.write(baos.toByteArray());
	    }
	}
}
