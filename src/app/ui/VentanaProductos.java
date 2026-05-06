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

    private void cargarTabla() {
        modelo.setRowCount(0);
        List<producto> productos = dao.obtenerTodos();
        for(producto p : productos){
            modelo.addRow(new Object[]{p.getId_producto(), p.getNombre_prod(), p.getPrecio(), p.getStock()});
        }
    }

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

    private void limpiarCampos() {
        txtNombre.setText("");
        txtPrecio.setText("");
        txtStock.setText("");
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf()); }
        catch(Exception ex){ ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new VentanaProductos().setVisible(true));
    }
}
