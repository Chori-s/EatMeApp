package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import app.logica.ProductoDAO;
import app.modelo.producto;

public class VentanaProductos extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtPrecio, txtStock;
    private ProductoDAO dao = new ProductoDAO();

    /**
     * VentanaProductos — Pantalla de gestión de productos en EatMe.
     *
     * Permite al administrador visualizar el catálogo completo de productos
     * en una tabla, añadir nuevos productos introduciendo nombre, precio y stock,
     * y eliminar productos existentes seleccionándolos en la tabla.
     *
     * Nota: esta clase corresponde a una versión preliminar del módulo de
     * administración. La gestión de productos en la versión final se integra
     * dentro del panel de pestañas de VentanaAdminControl.
     *
     * @author Iván
     */
    
    /**
     * Constructor principal de VentanaProductos.
     *
     * Inicializa la interfaz con el panel superior de formulario,
     * la tabla de productos y los botones de acción.
     */
    public VentanaProductos() {
        setTitle("Gestión de Productos - EatMe");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(43,43,43));

        JPanel panelSuperior = new JPanel(new GridLayout(2, 5, 10, 10));
        panelSuperior.setBorder(BorderFactory.createTitledBorder("➕ Datos del producto"));
        panelSuperior.setBackground(new Color(60,63,65));

        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        txtStock = new JTextField();

        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar seleccionado");
        JButton btnVolver = new JButton("Volver");
        JButton btnCerrarSesion = new JButton("Cerrar sesión");

        panelSuperior.add(new JLabel("Nombre:"){{setForeground(Color.WHITE);}});
        panelSuperior.add(txtNombre);
        panelSuperior.add(new JLabel("Precio:"){{setForeground(Color.WHITE);}});
        panelSuperior.add(txtPrecio);
        panelSuperior.add(new JLabel("Stock:"){{setForeground(Color.WHITE);}});
        panelSuperior.add(txtStock);
        panelSuperior.add(btnAgregar);
        panelSuperior.add(btnEliminar);
        panelSuperior.add(btnVolver);
        panelSuperior.add(btnCerrarSesion);

        add(panelSuperior, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Stock"}, 0);
        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(230,230,230));

        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnVolver.addActionListener(e -> this.dispose());
        btnCerrarSesion.addActionListener(e -> {
            this.dispose();
            VentanaLogin.abrirLogin();
        });

        cargarTabla();
    }

    /**
     * Carga todos los productos desde la base de datos y los muestra
     * en la tabla con columnas ID, Nombre, Precio y Stock.
     * Limpia las filas existentes antes de recargar.
     */
    private void cargarTabla() {
        modelo.setRowCount(0);
        List<producto> productos = dao.obtenerTodos();
        for(producto p : productos){
            modelo.addRow(new Object[]{p.getId_producto(), p.getNombre_prod(), p.getPrecio(), p.getStock()});
        }
    }

    /**
     * Valida los campos del formulario y añade un nuevo producto
     * a la base de datos si el nombre no existe previamente.
     * Recarga la tabla y limpia los campos tras la inserción.
     */
    private void agregarProducto() {
        String nombre = txtNombre.getText();
        double precio;
        int stock;
        try {
            precio = Double.parseDouble(txtPrecio.getText());
            stock = Integer.parseInt(txtStock.getText());
        } catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this,"Precio o stock inválido");
            return;
        }
        if(dao.obtenerTodos().stream().anyMatch(p -> p.getNombre_prod().equalsIgnoreCase(nombre))){
            JOptionPane.showMessageDialog(this,"El producto ya existe");
            return;
        }
        producto nuevo = new producto(0, nombre, precio, stock);
        dao.insertar(nuevo);
        cargarTabla();
        limpiarCampos();
        JOptionPane.showMessageDialog(this,"Producto agregado correctamente");
    }

    /**
     * Elimina el producto seleccionado en la tabla.
     * Muestra un aviso si no hay ninguna fila seleccionada.
     * Recarga la tabla tras la eliminación.
     */
    private void eliminarProducto() {
        int fila = tabla.getSelectedRow();
        if(fila == -1){
            JOptionPane.showMessageDialog(this,"Selecciona un producto para eliminar");
            return;
        }
        int id = (int) modelo.getValueAt(fila,0);
        dao.eliminar(id);
        cargarTabla();
        JOptionPane.showMessageDialog(this,"Producto eliminado correctamente");
    }

    /**
     * Limpia los campos de texto del formulario de entrada
     * tras una inserción correcta.
     */
    private void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }

    /**
     * Método main para pruebas en desarrollo.
     * Aplica FlatDarkLaf y lanza la ventana directamente sin pasar por el login.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf()); }
        catch(Exception ex){ ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new VentanaProductos().setVisible(true));
    }
}
