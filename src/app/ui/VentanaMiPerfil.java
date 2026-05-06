package app.ui;

import javax.swing.*;
import java.awt.*;
import app.logica.UsuarioDAO;
import app.modelo.Usuario;
import app.utils.LanguageManager;

/**
 * VentanaMiPerfil — Ventana para editar los datos personales del usuario.
 *
 * Permite cambiar nombre y contraseña. El email no se puede modificar
 * porque es el identificador único de la cuenta. Si el usuario tiene rol
 * ADMIN, aparece un botón para acceder al panel de administración.
 * Incluye botón de idioma ES/EN que reabre la ventana en el nuevo idioma.
 *
 * @author EatMe Team
 */
public class VentanaMiPerfil extends JFrame {

    private JTextField txtNombre, txtEmail;
    private JPasswordField txtPass;
    private Usuario usuario;
    private UsuarioDAO dao = new UsuarioDAO();

    /**
     * Constructor que monta la ventana con los datos del usuario actual.
     *
     * @param usuario El usuario que va a editar su perfil
     */
    public VentanaMiPerfil(Usuario usuario) {
        this.usuario = usuario;

        setTitle(LanguageManager.getTexto("perfil") + " - EatMe");
        setSize(460, 360);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(UIConstants.BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 14, 8, 14);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        add(lbl(LanguageManager.getTexto("nombre") + ":"), gbc);
        gbc.gridx = 1;
        txtNombre = campo(usuario.getNombre());
        add(txtNombre, gbc);

        // Email (solo lectura)
        gbc.gridx = 0; gbc.gridy++;
        add(lbl(LanguageManager.getTexto("correo") + ":"), gbc);
        gbc.gridx = 1;
        txtEmail = campo(usuario.getEmail());
        txtEmail.setEditable(false);
        txtEmail.setForeground(new Color(120, 120, 140));
        add(txtEmail, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy++;
        add(lbl(LanguageManager.getTexto("contraseña") + ":"), gbc);
        gbc.gridx = 1;
        txtPass = new JPasswordField(usuario.getContrasena());
        txtPass.setBackground(UIConstants.CARD);
        txtPass.setForeground(UIConstants.TEXT);
        txtPass.setCaretColor(UIConstants.TEXT);
        txtPass.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(txtPass, gbc);

        // Botones guardar / volver
        gbc.gridx = 0; gbc.gridy++;
        JButton btnGuardar = btn("Guardar", UIConstants.PRIMARY);
        add(btnGuardar, gbc);

        gbc.gridx = 1;
        JButton btnVolver = btn(LanguageManager.getTexto("cerrar"), UIConstants.SECONDARY);
        add(btnVolver, gbc);

        // Botón admin solo para ADMIN
        if ("ADMIN".equals(usuario.getRol())) {
            gbc.gridx = 0; gbc.gridy++;
            gbc.gridwidth = 2;
            JButton btnAdmin = btn(LanguageManager.getTexto("admin"), new Color(120, 60, 200));
            add(btnAdmin, gbc);
            gbc.gridwidth = 1;
            btnAdmin.addActionListener(e -> new VentanaAdminControl(usuario).setVisible(true));
        }

        // Botón de idioma
        gbc.gridx = 0; gbc.gridy++;
        gbc.gridwidth = 2;
        String idiomaActivo = LanguageManager.getIdioma();
        JButton btnIdioma = btn(idiomaActivo.equals("es") ? "EN" : "ES", UIConstants.BORDER);
        btnIdioma.setForeground(UIConstants.TEXT);
        add(btnIdioma, gbc);

        btnGuardar.addActionListener(e -> guardarCambios());
        btnVolver.addActionListener(e  -> dispose());
        btnIdioma.addActionListener(e  -> {
            String nuevo = LanguageManager.getIdioma().equals("es") ? "en" : "es";
            LanguageManager.setIdioma(nuevo);
            dispose();
            new VentanaMiPerfil(usuario).setVisible(true);
        });
    }

    private JLabel lbl(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(UIConstants.TEXT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return l;
    }

    private JTextField campo(String valor) {
        JTextField f = new JTextField(valor);
        f.setBackground(UIConstants.CARD);
        f.setForeground(UIConstants.TEXT);
        f.setCaretColor(UIConstants.TEXT);
        f.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return f;
    }

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
     * Valida los campos y guarda nombre y contraseña en la BD.
     */
    private void guardarCambios() {
        String nombre = txtNombre.getText().trim();
        String pass   = new String(txtPass.getPassword()).trim();
        if (nombre.isEmpty() || pass.isEmpty()) {
            Toast.show(this, LanguageManager.getTexto("camposObligatorios")); return;
        }
        usuario.setNombre(nombre);
        usuario.setContrasena(pass);
        dao.actualizar(usuario);
        Toast.show(this, LanguageManager.getTexto("datosActualizados"));
        dispose();
    }
}
