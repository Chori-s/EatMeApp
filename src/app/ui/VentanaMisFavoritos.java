package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import app.logica.FavoritosDAO;
import app.logica.CarritoDAO;
import app.modelo.producto;
import app.modelo.Usuario;
import app.utils.LanguageManager;

/**
 * VentanaMisFavoritos — Diálogo modal con la lista de favoritos del usuario.
 *
 * Permite ver los productos favoritos, quitarlos de favoritos o añadirlos
 * directamente al carrito sin volver al catálogo.
 * Incluye botón de idioma ES/EN que reabre el diálogo en el nuevo idioma.
 *
 * @author EatMe Team
 */
public class VentanaMisFavoritos extends JDialog {

    private Usuario usuario;
    private FavoritosDAO favDAO   = new FavoritosDAO();
    private CarritoDAO carritoDAO = new CarritoDAO();

    private JTable tabla;
    private DefaultTableModel modelo;

    /**
     * Constructor que monta el diálogo de favoritos.
     *
     * @param owner   Ventana padre
     * @param usuario Usuario cuyos favoritos se van a mostrar
     */
    public VentanaMisFavoritos(Frame owner, Usuario usuario) {
        super(owner, LanguageManager.getTexto("misFavoritos") + " - EatMe", true);
        this.usuario = usuario;
        setSize(760, 440);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(UIConstants.BG);

        modelo = new DefaultTableModel(
            new String[]{
                LanguageManager.getTexto("colId"),
                LanguageManager.getTexto("colNombre"),
                LanguageManager.getTexto("colPrecio"),
                LanguageManager.getTexto("colStock")
            }, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int c) {
                return switch (c) {
                    case 0 -> Integer.class;
                    case 2 -> Double.class;
                    case 3 -> Integer.class;
                    default -> String.class;
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
        // Cabecera visible con PRIMARY
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

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottom.setBackground(UIConstants.BG);

        JButton btnAnadir  = btn(LanguageManager.getTexto("anadirCarrito"),  UIConstants.SUCCESS);
        JButton btnQuitar  = btn(LanguageManager.getTexto("quitarFavorito"), UIConstants.DANGER);
        JButton btnCerrar  = btn(LanguageManager.getTexto("cerrar"),         UIConstants.SECONDARY);

        String idiomaActivo = LanguageManager.getIdioma();
        JButton btnIdioma = btn(idiomaActivo.equals("es") ? "EN" : "ES", UIConstants.BORDER);
        btnIdioma.setForeground(UIConstants.TEXT);

        bottom.add(btnAnadir);
        bottom.add(btnQuitar);
        bottom.add(btnIdioma);
        bottom.add(btnCerrar);
        add(bottom, BorderLayout.SOUTH);

        cargarFavoritos();

        btnAnadir.addActionListener(e  -> anadirAlCarrito());
        btnQuitar.addActionListener(e  -> quitarSeleccion());
        btnCerrar.addActionListener(e  -> dispose());
        btnIdioma.addActionListener(e  -> {
            String nuevo = LanguageManager.getIdioma().equals("es") ? "en" : "es";
            LanguageManager.setIdioma(nuevo);
            dispose();
            new VentanaMisFavoritos(owner, usuario).setVisible(true);
        });
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

    /** Carga los favoritos del usuario desde la BD. */
    private void cargarFavoritos() {
        modelo.setRowCount(0);
        for (producto p : favDAO.obtenerFavoritosUsuario(usuario.getId_usuario()))
            modelo.addRow(new Object[]{p.getId_producto(), p.getNombre_prod(), p.getPrecio(), p.getStock()});
    }

    private void quitarSeleccion() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) { Toast.show(this, LanguageManager.getTexto("seleccionaProducto")); return; }
        favDAO.quitarFavorito(usuario.getId_usuario(), (int) modelo.getValueAt(fila, 0));
        cargarFavoritos();
        Toast.show(this, LanguageManager.getTexto("favoritoEliminado"));
    }

    /** Añade el favorito seleccionado al carrito pidiendo cantidad. */
    private void anadirAlCarrito() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) { Toast.show(this, LanguageManager.getTexto("seleccionaProducto")); return; }
        int idProd = (int) modelo.getValueAt(fila, 0);
        int stock  = (int) modelo.getValueAt(fila, 3);
        String s   = JOptionPane.showInputDialog(this, LanguageManager.getTexto("colCantidad") + ":", "1");
        try {
            int cantidad = Integer.parseInt(s);
            if (cantidad <= 0 || cantidad > stock) { Toast.show(this, LanguageManager.getTexto("errorCantidad")); return; }
            carritoDAO.agregarOActualizar(usuario.getId_usuario(), idProd, cantidad);
            Toast.show(this, LanguageManager.getTexto("añadidoCarrito"));
        } catch (NumberFormatException ex) {
            Toast.show(this, LanguageManager.getTexto("errorCantidad"));
        }
    }
}
