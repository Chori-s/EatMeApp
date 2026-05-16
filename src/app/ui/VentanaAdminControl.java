package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import app.logica.ProductoDAO;
import app.logica.UsuarioDAO;
import app.logica.PedidoDAO;
import app.modelo.producto;
import app.modelo.Usuario;
import java.util.List;
import app.utils.LanguageManager;
import app.utils.ProcessManager;

/**
 * VentanaAdminControl — Panel de administración de EatMe.
 *
 * Solo accesible para usuarios con rol ADMIN. Tres pestañas:
 *   - Productos: ver, añadir, editar y eliminar del catálogo.
 *     La tabla se actualiza automáticamente después de cada operación,
 *     sin necesidad de botón Refrescar.
 *   - Usuarios: ver y eliminar usuarios registrados.
 *   - Pedidos: ver todos los pedidos y exportarlos a PDF.
 *
 * El nombre del admin es dinámico: viene del objeto Usuario recibido.
 * Las cabeceras de las tablas usan color PRIMARY (visible) en vez de gris.
 *
 * @author Iván
 */
public class VentanaAdminControl extends JFrame {

    private JTabbedPane tabs;
    private ProductoDAO productoDAO = new ProductoDAO();
    private UsuarioDAO usuarioDAO   = new UsuarioDAO();
    private PedidoDAO pedidoDAO     = new PedidoDAO();

    private JTable tblProductos, tblUsuarios, tblPedidos;
    private DefaultTableModel modeloProductos, modeloUsuarios, modeloPedidos;

    private Usuario admin;

    /**
     * Constructor que monta el panel de administración.
     * Cierra la ventana si el usuario no tiene rol ADMIN.
     *
     * @param admin El usuario ADMIN que accede al panel
     */
    public VentanaAdminControl(Usuario admin) {
        this.admin = admin;

        if (!admin.getRol().equals("ADMIN")) {
            JOptionPane.showMessageDialog(this, "Acceso denegado.");
            dispose(); return;
        }

        setTitle(LanguageManager.getTexto("admin") + " - EatMe");
        setSize(980, 660);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(UIConstants.BG);

        // Barra superior con nombre dinámico del admin y botón cerrar sesión
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UIConstants.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JLabel lblTitulo = new JLabel(LanguageManager.getTexto("admin") + " — " + admin.getNombre());
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        topBar.add(lblTitulo, BorderLayout.WEST);

        JButton btnCerrarSesion = new JButton(LanguageManager.getTexto("cerrarSesion"));
        btnCerrarSesion.setBackground(UIConstants.DANGER);
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCerrarSesion.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        topBar.add(btnCerrarSesion, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Pestañas con fondo oscuro
        tabs = new JTabbedPane();
        tabs.setBackground(UIConstants.BG);
        tabs.setForeground(UIConstants.TEXT);
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        initProductosTab();
        initUsuariosTab();
        initPedidosTab();
        add(tabs, BorderLayout.CENTER);

        btnCerrarSesion.addActionListener(e -> { VentanaLogin.abrirLogin(); dispose(); });
    }

    // ── PESTAÑA PRODUCTOS ─────────────────────────────────────────────────────

    /**
     * Inicializa la pestaña de productos. No hay botón Refrescar:
     * la tabla se actualiza automáticamente después de cada operación.
     */
    private void initProductosTab() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(UIConstants.BG);

        modeloProductos = new DefaultTableModel(
            new String[]{
                LanguageManager.getTexto("colId"),
                LanguageManager.getTexto("colNombre"),
                LanguageManager.getTexto("colPrecio"),
                LanguageManager.getTexto("colStock")
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Integer.class : (c == 2 ? Double.class : (c == 3 ? Integer.class : String.class));
            }
        };
        tblProductos = styledTable(modeloProductos);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        top.setBackground(UIConstants.BG);
        JButton btnAdd  = btn(LanguageManager.getTexto("añadirProducto"), UIConstants.SUCCESS);
        JButton btnEdit = btn(LanguageManager.getTexto("editar"),          UIConstants.PRIMARY);
        JButton btnDel  = btn(LanguageManager.getTexto("eliminar"),        UIConstants.DANGER);
        top.add(btnAdd); top.add(btnEdit); top.add(btnDel);

        p.add(top, BorderLayout.NORTH);
        p.add(darkScroll(tblProductos), BorderLayout.CENTER);

        btnAdd.addActionListener(e  -> crearProducto());
        btnEdit.addActionListener(e -> editarProducto());
        btnDel.addActionListener(e  -> eliminarProducto());

        tabs.add(LanguageManager.getTexto("productos"), p);
        cargarProductos();
    }

    // ── PESTAÑA USUARIOS ──────────────────────────────────────────────────────

    /**
     * Inicializa la pestaña de usuarios. La tabla se actualiza automáticamente
     * al eliminar un usuario.
     */
    private void initUsuariosTab() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(UIConstants.BG);

        modeloUsuarios = new DefaultTableModel(
            new String[]{
                LanguageManager.getTexto("colId"),
                LanguageManager.getTexto("colNombre"),
                LanguageManager.getTexto("colEmail")
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) { return c == 0 ? Integer.class : String.class; }
        };
        tblUsuarios = styledTable(modeloUsuarios);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        top.setBackground(UIConstants.BG);
        JButton btnDel = btn(LanguageManager.getTexto("eliminar") + " " + LanguageManager.getTexto("usuarios").toLowerCase(),
            UIConstants.DANGER);
        top.add(btnDel);

        p.add(top, BorderLayout.NORTH);
        p.add(darkScroll(tblUsuarios), BorderLayout.CENTER);

        btnDel.addActionListener(e -> eliminarUsuario());

        tabs.add(LanguageManager.getTexto("usuarios"), p);
        cargarUsuarios();
    }

    // ── PESTAÑA PEDIDOS ───────────────────────────────────────────────────────

    /**
     * Inicializa la pestaña de pedidos. Exporta a PDF usando ProcessManager.
     */
    private void initPedidosTab() {
        JPanel p = new JPanel(new BorderLayout(0, 0));
        p.setBackground(UIConstants.BG);

        modeloPedidos = new DefaultTableModel(
            new String[]{
                LanguageManager.getTexto("colId"),
                LanguageManager.getTexto("colUsuario"),
                LanguageManager.getTexto("colProducto"),
                LanguageManager.getTexto("colCantidad"),
                LanguageManager.getTexto("colFecha")
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblPedidos = styledTable(modeloPedidos);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 10));
        top.setBackground(UIConstants.BG);
        JButton btnExport = btn(LanguageManager.getTexto("exportar"), UIConstants.SUCCESS);
        top.add(btnExport);

        p.add(top, BorderLayout.NORTH);
        p.add(darkScroll(tblPedidos), BorderLayout.CENTER);

        btnExport.addActionListener(e -> exportarPedidos());

        tabs.add(LanguageManager.getTexto("pedidos"), p);
        cargarPedidos();
    }

    // ── HELPERS DE ESTILO ─────────────────────────────────────────────────────

    private JButton btn(String texto, Color bg) {
        JButton b = new JButton(texto);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /**
     * Crea una JTable con el estilo oscuro y las cabeceras en color PRIMARY visible.
     */
    private JTable styledTable(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(30);
        t.setBackground(UIConstants.CARD);
        t.setForeground(UIConstants.TEXT);
        t.setSelectionBackground(UIConstants.SELECT);
        t.setSelectionForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Cabecera con color PRIMARY visible, no gris
        t.getTableHeader().setBackground(UIConstants.PRIMARY);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setGridColor(UIConstants.BORDER);
        t.setShowVerticalLines(false);
        return t;
    }

    /**
     * Envuelve una tabla en un JScrollPane con fondo oscuro.
     * Sin esto, Nimbus pinta el área vacía de la tabla de gris claro.
     */
    private JScrollPane darkScroll(JTable tabla) {
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(UIConstants.BG);
        scroll.setBackground(UIConstants.BG);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        return scroll;
    }

    // ── LÓGICA PRODUCTOS ──────────────────────────────────────────────────────

    /** Recarga los productos desde la BD. Se llama automáticamente tras cada operación. */
    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        for (producto p : productoDAO.obtenerTodos())
            modeloProductos.addRow(new Object[]{
                p.getId_producto(), p.getNombre_prod(), p.getPrecio(), p.getStock()
            });
    }

    /** Abre diálogos para crear un nuevo producto y lo inserta en la BD. */
    private void crearProducto() {
        String nombre  = JOptionPane.showInputDialog(this, LanguageManager.getTexto("nombrePregunta"));
        if (nombre == null || nombre.isBlank()) return;
        String sPrecio = JOptionPane.showInputDialog(this, LanguageManager.getTexto("precioPregunta"));
        String sStock  = JOptionPane.showInputDialog(this, LanguageManager.getTexto("stockPregunta"));
        try {
            productoDAO.insertar(new producto(0, nombre.trim(),
                Double.parseDouble(sPrecio), Integer.parseInt(sStock)));
            cargarProductos();
            Toast.show(this, LanguageManager.getTexto("productoAñadido"));
        } catch (Exception ex) {
            Toast.show(this, LanguageManager.getTexto("errorDatos"));
        }
    }

    /** Permite editar el producto seleccionado. */
    private void editarProducto() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) { Toast.show(this, LanguageManager.getTexto("seleccionaProducto")); return; }
        int id = (int) modeloProductos.getValueAt(fila, 0);
        producto prod = productoDAO.obtenerPorId(id);
        if (prod == null) return;
        String nombre  = JOptionPane.showInputDialog(this, LanguageManager.getTexto("nombrePregunta"),  prod.getNombre_prod());
        String sPrecio = JOptionPane.showInputDialog(this, LanguageManager.getTexto("precioPregunta"), String.valueOf(prod.getPrecio()));
        String sStock  = JOptionPane.showInputDialog(this, LanguageManager.getTexto("stockPregunta"),   String.valueOf(prod.getStock()));
        try {
            prod.setNombre_prod(nombre);
            prod.setPrecio(Double.parseDouble(sPrecio));
            prod.setStock(Integer.parseInt(sStock));
            productoDAO.actualizarStock(prod.getId_producto(), prod.getStock());
            cargarProductos();
            Toast.show(this, LanguageManager.getTexto("productoActualizado"));
        } catch (Exception ex) {
            Toast.show(this, LanguageManager.getTexto("errorDatos"));
        }
    }

    /** Elimina el producto seleccionado tras confirmación. */
    private void eliminarProducto() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) { Toast.show(this, LanguageManager.getTexto("seleccionaProducto")); return; }
        int id = (int) modeloProductos.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this,
            LanguageManager.getTexto("confirmarEliminar") + " ID " + id + "?",
            LanguageManager.getTexto("confirmar"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            productoDAO.eliminar(id);
            cargarProductos();
            Toast.show(this, LanguageManager.getTexto("productoEliminado"));
        }
    }

    // ── LÓGICA USUARIOS ───────────────────────────────────────────────────────

    /** Recarga los usuarios desde la BD. */
    private void cargarUsuarios() {
        modeloUsuarios.setRowCount(0);
        for (Usuario u : usuarioDAO.obtenerTodos())
            modeloUsuarios.addRow(new Object[]{u.getId_usuario(), u.getNombre(), u.getEmail()});
    }

    /** Elimina el usuario seleccionado tras confirmación. */
    private void eliminarUsuario() {
        int fila = tblUsuarios.getSelectedRow();
        if (fila == -1) { Toast.show(this, LanguageManager.getTexto("seleccionaUsuario")); return; }
        int id = (int) modeloUsuarios.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(this,
            LanguageManager.getTexto("confirmarEliminar") + " ID " + id + "?",
            LanguageManager.getTexto("confirmar"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            usuarioDAO.eliminar(id);
            cargarUsuarios();
            Toast.show(this, LanguageManager.getTexto("usuarioEliminado"));
        }
    }

    // ── LÓGICA PEDIDOS ────────────────────────────────────────────────────────

    /** Recarga todos los pedidos desde la BD. */
    private void cargarPedidos() {
        modeloPedidos.setRowCount(0);
        for (app.modelo.Pedido p : pedidoDAO.obtenerTodos())
            modeloPedidos.addRow(new Object[]{
                p.getId_pedido(), p.getId_usuario(),
                p.getNombreProducto(), p.getCantidad(), p.getFecha()
            });
    }

    /**
     * Exporta todos los pedidos a PDF usando ProcessManager.
     * ProcessManager usa ProcessBuilder internamente (requisito técnico del proyecto).
     */
    private void exportarPedidos() {
        String ruta = ProcessManager.exportarPedidosPDF();
        Toast.show(this, LanguageManager.getTexto("pdfExportado") + " " + ruta);
    }
}
