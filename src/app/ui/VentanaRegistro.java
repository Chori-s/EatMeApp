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
 * Valida todos los campos antes de llamar al DAO.
 *
 * @author EatMe Team
 */
public class VentanaRegistro extends JFrame {

    private JTextField txtNombre, txtCorreo;
    private JPasswordField txtPass, txtPassConfirm;
    private JButton btnRegistrar, btnVolver;
    private UsuarioDAO dao = new UsuarioDAO();

    public VentanaRegistro() {
        setTitle("EatMe — " + LanguageManager.getTexto("registro"));
        setSize(420, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(UIConstants.BG);

        // Logo
        JLabel lblTitulo = new JLabel("EatMe");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setBounds(140, 25, 180, 40);
        lblTitulo.setForeground(UIConstants.PRIMARY);
        add(lblTitulo);

        // Subtítulo
        JLabel lblSub = new JLabel(LanguageManager.getTexto("registro"));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(140, 140, 160));
        lblSub.setBounds(155, 62, 160, 20);
        add(lblSub);

        // Campos del formulario
        int y = 100, gap = 62;
        txtNombre      = addField(LanguageManager.getTexto("nombre") + ":",            50, y);
        txtCorreo      = addField(LanguageManager.getTexto("correo") + " (@gmail.com):", 50, y + gap);
        txtPass        = addPassField(LanguageManager.getTexto("contrasena") + ":",     50, y + gap * 2);
        txtPassConfirm = addPassField(LanguageManager.getTexto("confirmarContrasena") + ":", 50, y + gap * 3);

        // Botón registrarse
        btnRegistrar = new JButton(LanguageManager.getTexto("registro"));
        btnRegistrar.setBounds(50, 360, 320, 44);
        btnRegistrar.setBackground(UIConstants.PRIMARY);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(btnRegistrar);

        // Botón volver
        btnVolver = new JButton("<- " + LanguageManager.getTexto("login"));
        btnVolver.setBounds(50, 415, 320, 36);
        btnVolver.setBackground(UIConstants.CARD);
        btnVolver.setForeground(UIConstants.TEXT);
        btnVolver.setFocusPainted(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(btnVolver);

        // Botón idioma
        String idiomaActual = LanguageManager.getTexto("login").equals("Login") ? "en" : "es";
        JButton btnIdioma = new JButton(idiomaActual.equals("es") ? "EN" : "ES");
        btnIdioma.setBounds(320, 480, 60, 28);
        btnIdioma.setBackground(UIConstants.BORDER);
        btnIdioma.setForeground(UIConstants.TEXT);
        btnIdioma.setFocusPainted(false);
        btnIdioma.setBorderPainted(false);
        btnIdioma.setFont(new Font("Segoe UI", Font.BOLD, 11));
        add(btnIdioma);

        btnRegistrar.addActionListener(e -> registrar());
        btnVolver.addActionListener(e -> { VentanaLogin.abrirLogin(); dispose(); });
        btnIdioma.addActionListener(e -> {
            String nuevoIdioma = idiomaActual.equals("es") ? "en" : "es";
            LanguageManager.setIdioma(nuevoIdioma);
            new VentanaRegistro().setVisible(true);
            dispose();
        });
    }

    /** Añade etiqueta + campo de texto al formulario. */
    private JTextField addField(String labelText, int x, int y) {
        JLabel lbl = new JLabel(labelText);
        lbl.setBounds(x, y, 320, 20);
        lbl.setForeground(UIConstants.TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lbl);
        JTextField f = new JTextField();
        f.setBounds(x, y + 22, 320, 34);
        f.setBackground(UIConstants.CARD);
        f.setForeground(UIConstants.TEXT);
        f.setCaretColor(UIConstants.TEXT);
        f.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(f);
        return f;
    }

    /** Añade etiqueta + campo de contraseña al formulario. */
    private JPasswordField addPassField(String labelText, int x, int y) {
        JLabel lbl = new JLabel(labelText);
        lbl.setBounds(x, y, 320, 20);
        lbl.setForeground(UIConstants.TEXT);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        add(lbl);
        JPasswordField f = new JPasswordField();
        f.setBounds(x, y + 22, 320, 34);
        f.setBackground(UIConstants.CARD);
        f.setForeground(UIConstants.TEXT);
        f.setCaretColor(UIConstants.TEXT);
        f.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER, 1));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        add(f);
        return f;
    }

    /**
     * Valida los campos y registra el nuevo usuario en la BD si todo es correcto.
     */
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
            Toast.show(this, "OK");
            VentanaLogin.abrirLogin();
            dispose();
        } else {
            Toast.show(this, LanguageManager.getTexto("errorEmail"));
        }
    }
}
