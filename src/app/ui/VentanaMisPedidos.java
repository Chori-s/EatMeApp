package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.List;
import app.logica.PedidoDAO;
import app.modelo.Pedido;
import app.modelo.Usuario;
import app.utils.LanguageManager;

/**
 * VentanaMisPedidos — Ventana con el historial de pedidos del usuario.
 *
 * Agrupa los pedidos por sesión de compra (mismo timestamp) y los muestra
 * como una única fila por sesión. Al pulsar "Ver Ticket" se muestran todos
 * los productos de esa sesión en un único recibo, en vez de uno por producto.
 *
 * NOTA: el Toast de "sin pedidos" se lanza con SwingUtilities.invokeLater
 * para evitar IllegalComponentStateException antes de que la ventana sea visible.
 *
 * @author Iván
 */
public class VentanaMisPedidos extends JFrame {

    private Usuario usuario;
    private PedidoDAO pedidoDAO = new PedidoDAO();
    private JTable tabla;
    private DefaultTableModel modelo;

    // Mapa: timestamp de sesión → lista de pedidos de esa sesión
    private LinkedHashMap<Timestamp, List<Pedido>> sesiones;

    // Lista ordenada de timestamps para mapear fila → sesión
    private List<Timestamp> ordenSesiones;

    /**
     * Constructor que inicializa la ventana.
     *
     * @param usuario El usuario cuyo historial se va a mostrar
     */
    public VentanaMisPedidos(Usuario usuario) {
        this.usuario = usuario;

        setTitle(LanguageManager.getTexto("pedidos") + " - EatMe");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UIConstants.BG);

        // Columnas del historial de sesiones: una fila por sesión de compra
        modelo = new DefaultTableModel(
            new String[]{"#", LanguageManager.getTexto("colFecha"),
                         LanguageManager.getTexto("colProducto"),
                         LanguageManager.getTexto("colCantidad"),
                         LanguageManager.getTexto("colTotal")}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setBackground(UIConstants.CARD);
        tabla.setForeground(UIConstants.TEXT);
        tabla.setSelectionBackground(UIConstants.SELECT);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Cabeceras con color PRIMARY visible (no gris)
        tabla.getTableHeader().setBackground(UIConstants.PRIMARY);
        tabla.getTableHeader().setForeground(Color.WHITE);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabla.setGridColor(UIConstants.BORDER);
        tabla.setShowVerticalLines(false);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(UIConstants.BG);
        scroll.setBackground(UIConstants.BG);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(UIConstants.BG);

        JButton btnVerTicket = new JButton(LanguageManager.getTexto("verTicket"));
        JButton btnCerrar    = new JButton(LanguageManager.getTexto("cerrar"));
        styleBtn(btnVerTicket, UIConstants.PRIMARY);
        styleBtn(btnCerrar,    UIConstants.SECONDARY);

        bottom.add(btnVerTicket);
        bottom.add(btnCerrar);
        add(bottom, BorderLayout.SOUTH);

        cargarHistorial();

        btnVerTicket.addActionListener(e -> abrirTicket());
        btnCerrar.addActionListener(e    -> dispose());
    }

    private void styleBtn(JButton b, Color bg) {
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    /**
     * Carga los pedidos desde la BD y los agrupa por sesión de compra (timestamp).
     * Muestra UNA FILA por sesión con el resumen: fecha, productos y total.
     * Los pedidos que no tienen timestamp exacto igual se agrupan por segundo.
     */
    private void cargarHistorial() {
        modelo.setRowCount(0);
        List<Pedido> todos = pedidoDAO.obtenerPorUsuario(usuario.getId_usuario());

        if (todos.isEmpty()) {
            SwingUtilities.invokeLater(() ->
                Toast.show(this, LanguageManager.getTexto("sinPedidos")));
            return;
        }

        // Agrupamos por timestamp exacto (todos los ítems de un procesado comparten el mismo)
        sesiones     = new LinkedHashMap<>();
        ordenSesiones = new ArrayList<>();

        for (Pedido p : todos) {
            Timestamp ts = p.getFecha();
            if (!sesiones.containsKey(ts)) {
                sesiones.put(ts, new ArrayList<>());
                ordenSesiones.add(ts);
            }
            sesiones.get(ts).add(p);
        }

        // Una fila por sesión
        int numSesion = ordenSesiones.size();
        for (Timestamp ts : ordenSesiones) {
            List<Pedido> items = sesiones.get(ts);

            // Resumen de productos: "Hamburguesa x2, Patatas x1"
            StringBuilder resumen = new StringBuilder();
            double total = 0;
            int totalCantidad = 0;
            for (Pedido p : items) {
                if (resumen.length() > 0) resumen.append(", ");
                resumen.append(p.getNombreProducto()).append(" x").append(p.getCantidad());
                total += p.getPrecio() * p.getCantidad();
                totalCantidad += p.getCantidad();
            }

            modelo.addRow(new Object[]{
                numSesion--,
                ts,
                resumen.toString(),
                totalCantidad,
                String.format("%.2f €", total)
            });
        }
    }

    /**
     * Muestra el ticket agrupado de la sesión seleccionada en la tabla.
     * El ticket incluye TODOS los productos de esa sesión de compra.
     */
    private void abrirTicket() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            Toast.show(this, LanguageManager.getTexto("seleccionaPedido"));
            return;
        }

        // La fila corresponde al índice en ordenSesiones
        Timestamp ts = ordenSesiones.get(fila);
        List<Pedido> items = sesiones.get(ts);

        // Construimos el ticket conjunto
        StringBuilder ticket = new StringBuilder();
        ticket.append("================================\n");
        ticket.append("   EatMe — Ticket de Compra\n");
        ticket.append("================================\n");
        ticket.append("Fecha: ").append(ts).append("\n");
        ticket.append("--------------------------------\n");

        double total = 0;
        for (Pedido p : items) {
            double sub = p.getPrecio() * p.getCantidad();
            total += sub;
            ticket.append(String.format("%-22s x%d\n", p.getNombreProducto(), p.getCantidad()));
            ticket.append(String.format("  %s €/ud.  ->  %.2f €\n", 
                String.format("%.2f", p.getPrecio()), sub));
        }

        ticket.append("--------------------------------\n");
        ticket.append(String.format("TOTAL: %.2f €\n", total));
        ticket.append("================================\n");
        ticket.append("      ¡Gracias por tu compra!\n");

        JTextArea area = new JTextArea(ticket.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(UIConstants.CARD);
        area.setForeground(UIConstants.TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JScrollPane sp = new JScrollPane(area);
        sp.getViewport().setBackground(UIConstants.CARD);

        JOptionPane.showMessageDialog(
            this, sp,
            LanguageManager.getTexto("ticketTitulo"),
            JOptionPane.PLAIN_MESSAGE
        );
    }
}
