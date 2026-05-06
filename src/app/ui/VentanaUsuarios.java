package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import app.logica.UsuarioDAO;
import app.modelo.Usuario;

public class VentanaUsuarios extends JFrame {

    private UsuarioDAO dao = new UsuarioDAO();
    private JTable tabla;
    private DefaultTableModel modelo;
    private JTextField txtNombre, txtEmail;
    private JPasswordField txtContrasena;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnCargar, btnVolver, btnCerrarSesion;

    public VentanaUsuarios() {
        setTitle("Gestión de Usuarios - EatMe");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(43,43,43));

        modelo = new DefaultTableModel(new Object[]{"ID","Nombre","Email","Contraseña"},0);
        tabla = new JTable(modelo);
        tabla.setRowHeight(28);
        tabla.setGridColor(new Color(230,230,230));
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        JPanel panelForm = new JPanel(new GridLayout(3,4,10,10));
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos del usuario"));
        panelForm.setBackground(new Color(60,63,65));

        txtNombre = new JTextField();
        txtEmail = new JTextField();
        txtContrasena = new JPasswordField();

        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnCargar = new JButton("Recargar");
        btnVolver = new JButton("Volver");
        btnCerrarSesion = new JButton("Cerrar sesión");

        panelForm.add(new JLabel("Nombre:"){{setForeground(Color.WHITE);}});
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Email:"){{setForeground(Color.WHITE);}});
        panelForm.add(txtEmail);
        panelForm.add(new JLabel("Contraseña:"){{setForeground(Color.WHITE);}});
        panelForm.add(txtContrasena);
        panelForm.add(btnAgregar);
        panelForm.add(btnActualizar);
        panelForm.add(btnEliminar);
        panelForm.add(btnCargar);
        panelForm.add(btnVolver);
        panelForm.add(btnCerrarSesion);

        add(panelForm, BorderLayout.SOUTH);

        cargarUsuarios();

        btnAgregar.addActionListener(e -> agregarUsuario());
        btnActualizar.addActionListener(e -> actualizarUsuario());
        btnEliminar.addActionListener(e -> eliminarUsuario());
        btnCargar.addActionListener(e -> cargarUsuarios());
        btnVolver.addActionListener(e -> this.dispose());
        btnCerrarSesion.addActionListener(e -> {
            this.dispose();
            VentanaLogin.abrirLogin();
        });

        tabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int fila = tabla.getSelectedRow();
                if(fila>=0){
                    txtNombre.setText(modelo.getValueAt(fila,1).toString());
                    txtEmail.setText(modelo.getValueAt(fila,2).toString());
                    txtContrasena.setText(modelo.getValueAt(fila,3).toString());
                }
            }
        });
    }

    private void cargarUsuarios() {
        modelo.setRowCount(0);
        List<Usuario> lista = dao.obtenerTodos();
        for(Usuario u : lista){
            modelo.addRow(new Object[]{u.getId_usuario(), u.getNombre(), u.getEmail(), u.getContrasena()});
        }
    }

    private void agregarUsuario() {
        if(txtNombre.getText().isEmpty() || txtEmail.getText().isEmpty() || txtContrasena.getText().isEmpty()){
            JOptionPane.showMessageDialog(this,"Todos los campos son obligatorios");
            return;
        }
        if(dao.obtenerTodos().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(txtEmail.getText()))){
            JOptionPane.showMessageDialog(this,"El email ya está registrado");
            return;
        }
        Usuario u = new Usuario(txtNombre.getText(), txtEmail.getText(), txtContrasena.getText());
        dao.insertar(u);
        cargarUsuarios();
        limpiarCampos();
    }

    private void actualizarUsuario() {
        int fila = tabla.getSelectedRow();
        if(fila<0){
            JOptionPane.showMessageDialog(this,"Selecciona un usuario para actualizar");
            return;
        }
        int id = (int) modelo.getValueAt(fila,0);
        Usuario u = new Usuario(id, txtNombre.getText(), txtEmail.getText(), txtContrasena.getText());
        dao.actualizar(u);
        cargarUsuarios();
        limpiarCampos();
    }

    private void eliminarUsuario() {
        int fila = tabla.getSelectedRow();
        if(fila<0){
            JOptionPane.showMessageDialog(this,"Selecciona un usuario para eliminar");
            return;
        }
        int id = (int) modelo.getValueAt(fila,0);
        dao.eliminar(id);
        cargarUsuarios();
        limpiarCampos();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtEmail.setText("");
        txtContrasena.setText("");
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf()); }
        catch(Exception ex){ ex.printStackTrace(); }
        SwingUtilities.invokeLater(() -> new VentanaUsuarios().setVisible(true));
    }
}
