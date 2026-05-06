package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import app.logica.ProductoDAO;
import app.logica.PedidoDAO;
import app.modelo.producto;
import app.modelo.Pedido;
import app.modelo.Usuario;

public class VentanaPedidos extends JFrame {

    private JTable tablaProductos;
    private JTable tablaHistorial;
    private DefaultTableModel modeloProductos;
    private DefaultTableModel modeloHistorial;
    private ProductoDAO productoDAO = new ProductoDAO();
    private PedidoDAO pedidoDAO = new PedidoDAO();
    private Usuario usuario;

    public VentanaPedidos(Usuario usuario) {
        this.usuario = usuario;

        setTitle("Pedidos - EatMe 🛒");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(43,43,43));

        // --- Tabla productos disponibles ---
        modeloProductos = new DefaultTableModel(new Object[]{"Producto 🍔","Cantidad","Precio €"},0){
            public boolean isCellEditable(int row,int col){ return col==1; }
        };
        tablaProductos = new JTable(modeloProductos);
        tablaProductos.setRowHeight(30);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaProductos.setGridColor(new Color(200,200,200));
        cargarProductos();

        JScrollPane scrollProductos = new JScrollPane(tablaProductos);
        scrollProductos.setBorder(BorderFactory.createTitledBorder("Productos disponibles"));
        add(scrollProductos, BorderLayout.NORTH);

        // --- Tabla historial de pedidos ---
        modeloHistorial = new DefaultTableModel(new Object[]{"Producto","Cantidad","Precio €","Fecha 🗓️"},0);
        tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setRowHeight(28);
        tablaHistorial.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaHistorial.setGridColor(new Color(180,180,180));
        JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
        scrollHistorial.setBorder(BorderFactory.createTitledBorder("Historial de pedidos"));
        add(scrollHistorial, BorderLayout.CENTER);
        cargarHistorial();

        // --- Panel inferior ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(new Color(60,63,65));
        JButton btnConfirmar = new JButton("Confirmar pedido ✅");
        JButton btnCerrar = new JButton("Cerrar sesión 🔒");
        panelInferior.add(btnConfirmar);
        panelInferior.add(btnCerrar);
        add(panelInferior, BorderLayout.SOUTH);

        // --- Eventos ---
        btnCerrar.addActionListener(e -> VentanaLogin.abrirLogin());

        btnConfirmar.addActionListener(e -> confirmarPedido());
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        List<producto> productos = productoDAO.obtenerTodos();
        for(producto p : productos){
            String nombre = p.getNombre_prod();
            if(p.getPrecio() < 5) nombre = "🥗 "+nombre;
            else if(p.getPrecio() < 10) nombre = "🍔 "+nombre;
            else nombre = "🍖 "+nombre;

            modeloProductos.addRow(new Object[]{nombre,0,p.getPrecio()});
        }
    }

    private void cargarHistorial() {
        modeloHistorial.setRowCount(0);
        List<Pedido> pedidos = pedidoDAO.obtenerPorUsuario(usuario.getId_usuario());
        for(Pedido p : pedidos){
            String nombre = p.getNombreProducto();
            if(p.getPrecio()<5) nombre = "🥗 "+nombre;
            else if(p.getPrecio()<10) nombre = "🍔 "+nombre;
            else nombre = "🍖 "+nombre;

            modeloHistorial.addRow(new Object[]{nombre, p.getCantidad(), p.getPrecio(), p.getFecha()});
        }
    }

    private void confirmarPedido() {
        boolean hayCantidad = false;
        for(int i=0;i<modeloProductos.getRowCount();i++){
            int cantidad=0;
            try { cantidad = Integer.parseInt(modeloProductos.getValueAt(i,1).toString()); } 
            catch(Exception e){ cantidad=0; }
            if(cantidad>0){
                hayCantidad = true;
                producto p = productoDAO.obtenerTodos().get(i);
                if(cantidad>p.getStock()){
                    JOptionPane.showMessageDialog(this,"Stock insuficiente de "+p.getNombre_prod());
                    return;
                }
            }
        }
        if(!hayCantidad){
            JOptionPane.showMessageDialog(this,"Selecciona al menos un producto");
            return;
        }

        // Insertar pedidos y actualizar stock
        for(int i=0;i<modeloProductos.getRowCount();i++){
            int cantidad=0;
            try { cantidad = Integer.parseInt(modeloProductos.getValueAt(i,1).toString()); } 
            catch(Exception e){ cantidad=0; }
            if(cantidad>0){
                producto p = productoDAO.obtenerTodos().get(i);
                pedidoDAO.insertar(new Pedido(usuario.getId_usuario(), p.getId_producto(), cantidad));
                productoDAO.actualizarStock(p.getId_producto(), p.getStock()-cantidad);
            }
        }

        JOptionPane.showMessageDialog(this,"Pedido confirmado 🎉");
        cargarProductos();
        cargarHistorial();
    }

    public static void main(String[] args) {
        Usuario demo = new Usuario(1,"Demo","demo@demo.com","1234");
        SwingUtilities.invokeLater(() -> new VentanaPedidos(demo).setVisible(true));
    }
}
