package app.ui;

import javax.swing.*;
import java.awt.*;
import app.logica.UsuarioDAO;
import app.utils.LanguageManager;

/**
 * VentanaRegistro — Pantalla de registro de nuevos usuarios en EatMe.
 *
 * Permite crear una cuenta con nombre, correo (@gmail.com) y contraseña
 * (con confirmación). Incluye botón de cambio de idioma ES/EN.
 * Al cambiar de idioma los textos se actualizan sin borrar lo que el usuario
 * haya escrito en los campos.
 *
 * @author Iván
 */
public class VentanaRegistro extends JFrame {

    private JTextField txtNombre, txtCorreo;
    private JPasswordField txtPass, txtPassConfirm;
    private JButton btnRegistrar, btnVolver, btnIdioma;

    // Etiquetas guardadas como campos para poder actualizarlas al cambiar idioma
    private JLabel lblNombre, lblCorreo, lblPass, lblPassConfirm;

    private UsuarioDAO dao = new UsuarioDAO();
    private String idiomaActivo = LanguageManager.getIdioma();

    /**
     * Constructor sin correo previo (acceso directo al registro).
     */
    public VentanaRegistro() {
        this("");
    }

    /**
     * Constructor que acepta un correo previo (viene de VentanaLogin).
     * El correo se pre-rellena para no perder lo que el usuario ya escribió.
     *
     * @param correoInicial Correo ya escrito en el login, puede ser vacío.
     */
    public VentanaRegistro(String correoInicial) {
        setTitle("EatMe — " + LanguageManager.getTexto("registro"));
        setSize(420, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(UIConstants.BG);

        JLabel lblTitulo = new JLabel("EatMe");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setBounds(140, 25, 180, 40);
        lblTitulo.setForeground(UIConstants.PRIMARY);
        add(lblTitulo);

        JLabel lblSub = new JLabel(LanguageManager.getTexto("registro"));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(140, 140, 160));
        lblSub.setBounds(155, 62, 160, 20);
        add(lblSub);

        int y = 100, gap = 62;

        // Nombre
        lblNombre = label(LanguageManager.getTexto("nombre") + ":", 50, y);
        txtNombre = textField(50, y + 22);

        // Correo
        lblCorreo = label(LanguageManager.getTexto("correo") + " (@gmail.com):", 50, y + gap);
        txtCorreo = textField(50, y + gap + 22);
        if (!correoInicial.isEmpty()) txtCorreo.setText(correoInicial);

        // Contraseña
        lblPass = label(LanguageManager.getTexto("contrasena") + ":", 50, y + gap * 2);
        txtPass = passField(50, y + gap * 2 + 22);

        // Confirmar contraseña
        lblPassConfirm = label(LanguageManager.getTexto("confirmarContrasena") + ":", 50, y + gap * 3);
        txtPassConfirm = passField(50, y + gap * 3 + 22);

        btnRegistrar = new JButton(LanguageManager.getTexto("registro"));
        btnRegistrar.setBounds(50, 360, 320, 44);
        btnRegistrar.setBackground(UIConstants.PRIMARY);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnRegistrar);

        btnVolver = new JButton("<- " + LanguageManager.getTexto("login"));
        btnVolver.setBounds(50, 415, 320, 36);
        btnVolver.setBackground(UIConstants.CARD);
        btnVolver.setForeground(UIConstants.TEXT);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnVolver);

        btnIdioma = new JButton(idiomaActivo.equals("es") ? "EN" : "ES");
        btnIdioma.setBounds(180, 465, 60, 28);
        btnIdioma.setBackground(UIConstants.BORDER);
        btnIdioma.setForeground(UIConstants.TEXT);
        btnIdioma.setFocusPainted(false);
        btnIdioma.setBorderPainted(false);
        btnIdioma.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnIdioma.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        add(btnIdioma);

        btnRegistrar.addActionListener(e -> registrar());
        btnVolver.addActionListener(e -> { VentanaLogin.abrirLogin(); dispose(); });
        btnIdioma.addActionListener(e -> cambiarIdioma());
    }

    /**
     * Cambia el idioma actualizando los textos EN LA MISMA VENTANA,
     * conservando todo lo que el usuario haya escrito en los campos.
     */
    private void cambiarIdioma() {
        idiomaActivo = idiomaActivo.equals("es") ? "en" : "es";
        LanguageManager.setIdioma(idiomaActivo);

        lblNombre.setText(LanguageManager.getTexto("nombre") + ":");
        lblCorreo.setText(LanguageManager.getTexto("correo") + " (@gmail.com):");
        lblPass.setText(LanguageManager.getTexto("contrasena") + ":");
        lblPassConfirm.setText(LanguageManager.getTexto("confirmarContrasena") + ":");
        btnRegistrar.setText(LanguageManager.getTexto("registro"));
        btnVolver.setText("<- " + LanguageManager.getTexto("login"));
        btnIdioma.setText(idiomaActivo.equals("es") ? "EN" : "ES");

        repaint();
    }

    private JLabel label(String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setBounds(x, y, 320, 20);
        lbl.setForeground(UIConstants.TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lbl);
        return lbl;
    }

    private JTextField textField(int x, int y) {
        JTextField f = new JTextField();
        f.setBounds(x, y, 320, 34);
        f.setBackground(UIConstants.CARD);
        f.setForeground(UIConstants.TEXT);
        f.setCaretColor(UIConstants.TEXT);
        f.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(f);
        return f;
    }

    private JPasswordField passField(int x, int y) {
        JPasswordField f = new JPasswordField();
        f.setBounds(x, y, 320, 34);
        f.setBackground(UIConstants.CARD);
        f.setForeground(UIConstants.TEXT);
        f.setCaretColor(UIConstants.TEXT);
        f.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(f);
        return f;
    }

    private void registrar() {
        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String pass   = new String(txtPass.getPassword()).trim();
        String passC  = new String(txtPassConfirm.getPassword()).trim();

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || passC.isEmpty()) {
            Toast.show(this, LanguageManager.getTexto("camposObligatorios")); return;
        }
        if (!correo.endsWith("@gmail.com")) {
            Toast.show(this, LanguageManager.getTexto("errorCorreo")); return;
        }
        if (!pass.equals(passC)) {
            Toast.show(this, LanguageManager.getTexto("errorContrasenas")); return;
        }

        if (dao.registrar(nombre, correo, pass)) {
            Toast.show(this, LanguageManager.getTexto("registroOk"));
            VentanaLogin.abrirLogin();
            dispose();
        } else {
            Toast.show(this, LanguageManager.getTexto("errorEmail"));
        }
    }
}