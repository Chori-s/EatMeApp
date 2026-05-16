package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

import app.logica.CarritoDAO;
import app.logica.PedidoDAO;
import app.logica.ProductoDAO;
import app.modelo.CarritoItem;
import app.modelo.Pedido;
import app.modelo.producto;
import app.modelo.Usuario;
import app.utils.LanguageManager;

/**
 * VentanaCarrito — Diálogo modal con el carrito de compra del usuario.
 *
 * Muestra los ítems del carrito con subtotales y total general.
 * Al procesar el pedido, todos los ítems del carrito se insertan con el
 * MISMO timestamp para que en "Mis Pedidos" aparezcan como un único
 * pedido agrupado con su ticket conjunto, en vez de uno por producto.
 *
 * @author Iván
 */
public class VentanaCarrito extends JDialog {

    private Usuario usuario;
    private CarritoDAO carritoDAO   = new CarritoDAO();
    private PedidoDAO pedidoDAO     = new PedidoDAO();
    private ProductoDAO productoDAO = new ProductoDAO();

    private JTable tabla;
    private DefaultTableModel modelo;

    /**
     * Constructor que monta el diálogo del carrito.
     *
     * @param owner   Ventana padre
     * @param usuario Usuario dueño del carrito
     */
    public VentanaCarrito(Frame owner, Usuario usuario) {
        super(owner, LanguageManager.getTexto("carrito") + " - EatMe", true);
        this.usuario = usuario;
        setSize(760, 480);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(UIConstants.BG);

        // Cabeceras de tabla traducidas
        modelo = new DefaultTableModel(
            new String[]{
                LanguageManager.getTexto("colId"),
                LanguageManager.getTexto("colProducto"),
                LanguageManager.getTexto("colCantidad"),
                LanguageManager.getTexto("colPrecioUnit"),
                LanguageManager.getTexto("colSubtotal")
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0, 2 -> Integer.class;
                    case 3, 4 -> Double.class;
                    default   -> String.class;
                };
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setBackground(UIConstants.CARD);
        tabla.setForeground(UIConstants.TEXT);
        tabla.setSelectionBackground(UIConstants.SELECT);
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Cabeceras con color PRIMARY visible
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

        // Panel inferior con botones
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(UIConstants.BG);

        JButton btnCambiarCant = new JButton(LanguageManager.getTexto("cambiarCantidad"));
        JButton btnEliminar    = new JButton(LanguageManager.getTexto("eliminarItem"));
        JButton btnVaciar      = new JButton(LanguageManager.getTexto("vaciarCarrito"));
        JButton btnProcesar    = new JButton(LanguageManager.getTexto("procesarPedido"));
        JButton btnCerrar      = new JButton(LanguageManager.getTexto("cerrar"));

        styleBtn(btnCambiarCant, UIConstants.PRIMARY);
        styleBtn(btnEliminar,    UIConstants.DANGER);
        styleBtn(btnVaciar,      UIConstants.DANGER);
        styleBtn(btnProcesar,    UIConstants.SUCCESS);
        styleBtn(btnCerrar,      UIConstants.SECONDARY);

        bottom.add(btnCambiarCant);
        bottom.add(btnEliminar);
        bottom.add(btnVaciar);
        bottom.add(btnProcesar);
        bottom.add(btnCerrar);
        add(bottom, BorderLayout.SOUTH);

        cargarCarrito();

        btnCambiarCant.addActionListener(e -> cambiarCantidad());
        btnEliminar.addActionListener(e    -> eliminarSeleccion());
        btnProcesar.addActionListener(e    -> procesarPedido());
        btnVaciar.addActionListener(e -> {
            carritoDAO.vaciarCarritoUsuario(usuario.getId_usuario());
            cargarCarrito();
            Toast.show(this, LanguageManager.getTexto("carritoVaciadoOk"));
        });
        btnCerrar.addActionListener(e -> dispose());
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
     * Recarga los ítems del carrito desde la BD y actualiza el título con el total.
     */
    private void cargarCarrito() {
        modelo.setRowCount(0);
        List<CarritoItem> items = carritoDAO.obtenerPorUsuario(usuario.getId_usuario());
        double total = 0;
        for (CarritoItem it : items) {
            double sub = it.getSubtotal();
            total += sub;
            modelo.addRow(new Object[]{
                it.getId_carrito(), it.getNombreProducto(),
                it.getCantidad(), it.getPrecioUnitario(), sub
            });
        }
        setTitle(LanguageManager.getTexto("carrito") + " - EatMe  |  "
            + LanguageManager.getTexto("totalCarrito") + " "
            + String.format("%.2f €", total));
    }

    private void eliminarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) { Toast.show(this, LanguageManager.getTexto("seleccionaItem")); return; }
        carritoDAO.eliminarPorId((int) modelo.getValueAt(fila, 0));
        cargarCarrito();
        Toast.show(this, LanguageManager.getTexto("itemEliminado"));
    }

    private void cambiarCantidad() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) { Toast.show(this, LanguageManager.getTexto("seleccionaItem")); return; }
        int idCarrito = (int) modelo.getValueAt(fila, 0);
        int actual    = (int) modelo.getValueAt(fila, 2);
        String s = JOptionPane.showInputDialog(this,
            LanguageManager.getTexto("nuevaCantidad") + " " + actual + "):", actual);
        try {
            int nueva = Integer.parseInt(s);
            if (nueva <= 0) { Toast.show(this, LanguageManager.getTexto("errorCantidad")); return; }
            carritoDAO.actualizarCantidad(idCarrito, nueva);
            cargarCarrito();
            Toast.show(this, LanguageManager.getTexto("cantidadActualizada"));
        } catch (NumberFormatException ex) {
            Toast.show(this, LanguageManager.getTexto("errorCantidad"));
        }
    }

    /**
     * Procesa el pedido completo del carrito:
     * 1. Verifica stock para todos los ítems.
     * 2. Genera un único timestamp para toda la sesión de compra.
     * 3. Inserta todos los ítems con ese mismo timestamp (pedido agrupado).
     * 4. Resta stock y vacía el carrito.
     *
     * Al compartir timestamp, VentanaMisPedidos puede agruparlos y mostrar
     * un ticket único con todos los productos de la sesión.
     */
    private void procesarPedido() {
        List<CarritoItem> items = carritoDAO.obtenerPorUsuario(usuario.getId_usuario());
        if (items.isEmpty()) { Toast.show(this, LanguageManager.getTexto("carritoVacio")); return; }

        // Verificar stock antes de insertar nada
        for (CarritoItem it : items) {
            producto prod = productoDAO.obtenerPorId(it.getId_producto());
            if (prod == null || prod.getStock() < it.getCantidad()) {
                Toast.show(this, LanguageManager.getTexto("stockInsuficiente") + " " + it.getNombreProducto());
                return;
            }
        }

        // Timestamp único para toda la sesión → agrupa todos los ítems en un pedido
        Timestamp sesion = new Timestamp(System.currentTimeMillis());

        for (CarritoItem it : items) {
            pedidoDAO.insertarConFecha(new Pedido(
                usuario.getId_usuario(), it.getId_producto(), it.getCantidad()
            ), sesion);
            producto prod = productoDAO.obtenerPorId(it.getId_producto());
            if (prod != null) {
                productoDAO.actualizarStock(it.getId_producto(), prod.getStock() - it.getCantidad());
            }
        }

        carritoDAO.vaciarCarritoUsuario(usuario.getId_usuario());
        cargarCarrito();
        Toast.show(this, LanguageManager.getTexto("pedidoOk"));
        dispose();
    }
}
