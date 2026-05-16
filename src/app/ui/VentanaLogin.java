package app.ui;

import javax.swing.*;
import java.awt.*;
import app.logica.UsuarioDAO;
import app.modelo.Usuario;
import app.utils.LanguageManager;

/**
 * VentanaLogin — Pantalla de inicio de sesión de EatMe.
 *
 * Primera ventana al arrancar la app. Permite autenticarse con email y contraseña,
 * o navegar al registro. Incluye botón de cambio de idioma ES/EN.
 * Al cambiar de idioma los textos se actualizan sin borrar lo que el usuario
 * haya escrito en los campos.
 *
 * @author EatMe Team
 */
public class VentanaLogin extends JFrame {

    private JTextField txtCorreo;
    private JPasswordField txtPass;
    private JButton btnLogin, btnRegistro, btnIdioma;
    private JLabel lblCorreo, lblPass;
    private UsuarioDAO dao = new UsuarioDAO();

    private String idiomaActivo = LanguageManager.getIdioma();

    public VentanaLogin() {
        setTitle("EatMe — Iniciar sesion");
        setSize(400, 530);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(UIConstants.BG);

        JLabel lblTitulo = new JLabel("EatMe");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(UIConstants.PRIMARY);
        lblTitulo.setBounds(130, 35, 200, 50);
        add(lblTitulo);

        JLabel lblSub = new JLabel("Tu app de comida favorita");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(140, 140, 160));
        lblSub.setBounds(100, 82, 220, 20);
        add(lblSub);

        // Campo correo
        lblCorreo = new JLabel(LanguageManager.getTexto("correo") + ":");
        lblCorreo.setBounds(50, 130, 300, 20);
        lblCorreo.setForeground(UIConstants.TEXT);
        lblCorreo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lblCorreo);
        txtCorreo = styledField();
        txtCorreo.setBounds(50, 153, 300, 36);
        add(txtCorreo);

        // Campo contraseña
        lblPass = new JLabel(LanguageManager.getTexto("contrasena") + ":");
        lblPass.setBounds(50, 205, 300, 20);
        lblPass.setForeground(UIConstants.TEXT);
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lblPass);
        txtPass = new JPasswordField();
        estiloCampo(txtPass);
        txtPass.setBounds(50, 228, 300, 36);
        add(txtPass);

        btnLogin = new JButton(LanguageManager.getTexto("login"));
        btnLogin.setBounds(50, 300, 300, 44);
        estiloBtnPrimario(btnLogin);
        add(btnLogin);

        btnRegistro = new JButton(LanguageManager.getTexto("registro"));
        btnRegistro.setBounds(50, 355, 300, 38);
        btnRegistro.setBackground(UIConstants.CARD);
        btnRegistro.setForeground(UIConstants.TEXT);
        btnRegistro.setFocusPainted(false);
        btnRegistro.setBorderPainted(false);
        btnRegistro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnRegistro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnRegistro);

        btnIdioma = new JButton(idiomaActivo.equals("es") ? "EN" : "ES");
        btnIdioma.setBounds(170, 410, 60, 28);
        btnIdioma.setBackground(UIConstants.BORDER);
        btnIdioma.setForeground(UIConstants.TEXT);
        btnIdioma.setFocusPainted(false);
        btnIdioma.setBorderPainted(false);
        btnIdioma.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnIdioma.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnIdioma);

        btnLogin.addActionListener(e -> login());
        btnRegistro.addActionListener(e -> {
            // Pasamos el correo ya escrito a la ventana de registro
            new VentanaRegistro().setVisible(true);
            dispose();
        });
        btnIdioma.addActionListener(e -> cambiarIdioma());
    }

    /**
     * Cambia el idioma actualizando los textos EN LA MISMA VENTANA,
     * sin destruirla ni borrar lo que el usuario haya escrito.
     */
    private void cambiarIdioma() {
        idiomaActivo = idiomaActivo.equals("es") ? "en" : "es";
        LanguageManager.setIdioma(idiomaActivo);

        lblCorreo.setText(LanguageManager.getTexto("correo") + ":");
        lblPass.setText(LanguageManager.getTexto("contrasena") + ":");
        btnLogin.setText(LanguageManager.getTexto("login"));
        btnRegistro.setText(LanguageManager.getTexto("registro"));
        btnIdioma.setText(idiomaActivo.equals("es") ? "EN" : "ES");

        repaint();
    }

    private JTextField styledField() {
        JTextField f = new JTextField();
        estiloCampo(f);
        return f;
    }

    private void estiloCampo(JTextField f) {
        f.setBackground(UIConstants.CARD);
        f.setForeground(UIConstants.TEXT);
        f.setCaretColor(UIConstants.TEXT);
        f.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }

    private void estiloBtnPrimario(JButton b) {
        b.setBackground(UIConstants.PRIMARY);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void login() {
        String correo = txtCorreo.getText().trim();
        String pass   = new String(txtPass.getPassword()).trim();

        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.show(this, LanguageManager.getTexto("camposObligatorios")); return;
        }
        if (!correo.endsWith("@gmail.com")) {
            Toast.show(this, LanguageManager.getTexto("errorCorreo")); return;
        }

        Usuario u = dao.login(correo, pass);
        if (u == null) {
            Toast.show(this, LanguageManager.getTexto("errorCredenciales")); return;
        }

        if (u.getRol().equals("ADMIN")) {
            UIUtils.fadeInWindow(new VentanaAdminControl(u));
        } else {
            UIUtils.fadeInWindow(new VentanaProductosUsuario(u));
        }
        dispose();
    }

    public static void abrirLogin() {
        UIUtils.fadeInWindow(new VentanaLogin());
    }
}