package app.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Set;

import app.logica.ProductoDAO;
import app.logica.CarritoDAO;
import app.modelo.producto;
import app.modelo.Usuario;
import app.utils.LanguageManager;

/**
 * VentanaProductosUsuario — Pantalla principal del usuario en EatMe.
 *
 * Muestra el catálogo de productos. Permite añadir al carrito, marcar
 * favoritos, ver pedidos y editar el perfil. El botón de idioma ES/EN
 * reconstruye los botones y las cabeceras de la tabla al instante.
 * El botón Panel Admin solo aparece si el usuario tiene rol ADMIN.
 * La tabla se actualiza automáticamente al volver del carrito o de favoritos.
 *
 * La carga del catálogo se realiza en un hilo de fondo mediante SwingWorker
 * para no bloquear el Event Dispatch Thread (EDT) durante la consulta a
 * Supabase. Mientras se carga, se muestra un spinner de espera en la tabla.
 *
 * @author EatMe Team
 */
public class VentanaProductosUsuario extends JFrame {

    private JTable tabla;
    private DefaultTableModel modelo;
    private JPanel panelBotones;
    private JScrollPane scroll;

    private ProductoDAO dao       = new ProductoDAO();
    private CarritoDAO carritoDAO = new CarritoDAO();

    private Usuario usuario;

    /** Idioma activo: "es" o "en". Se mantiene sincronizado con LanguageManager. */
    private String idiomaActivo = LanguageManager.getIdioma();

    /**
     * Constructor que inicializa la ventana principal.
     *
     * @param usuario El usuario que ha iniciado sesión
     */
    public VentanaProductosUsuario(Usuario usuario) {
        this.usuario = usuario;
        idiomaActivo = LanguageManager.getIdioma();

        setTitle("EatMe — " + usuario.getNombre());
        setSize(1020, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(UIConstants.BG);

        // Tabla del catálogo (el modelo se reconstruye al cambiar idioma)
        modelo = crearModelo();
        tabla  = crearTabla(modelo);

        scroll = new JScrollPane(tabla);
        scroll.getViewport().setBackground(UIConstants.BG);
        scroll.setBackground(UIConstants.BG);
        scroll.setBorder(BorderFactory.createLineBorder(UIConstants.BORDER));
        add(scroll, BorderLayout.CENTER);

        // Panel de botones (se reconstruye al cambiar idioma)
        panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 8));
        panelBotones.setBackground(new Color(20, 20, 35));
        add(panelBotones, BorderLayout.SOUTH);

        construirBotones();
        cargarTabla();
    }

    /**
     * Crea el modelo de tabla con las cabeceras en el idioma activo.
     * Se llama al iniciar y al cambiar de idioma.
     */
    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(
            new String[]{
                LanguageManager.getTexto("colId"),
                LanguageManager.getTexto("colNombre"),
                LanguageManager.getTexto("colPrecio"),
                LanguageManager.getTexto("colStock"),
                LanguageManager.getTexto("colFavorito")
            }, 0
        ) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
            @Override public Class<?> getColumnClass(int column) {
                return switch (column) {
                    case 2 -> Double.class;
                    case 3 -> Integer.class;
                    case 4 -> Boolean.class;
                    default -> Object.class;
                };
            }
        };
    }

    /**
     * Crea la JTable con el estilo oscuro y las cabeceras en color PRIMARY visible.
     */
    private JTable crearTabla(DefaultTableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(32);
        t.setBackground(UIConstants.CARD);
        t.setForeground(UIConstants.TEXT);
        t.setSelectionBackground(UIConstants.SELECT);
        t.setSelectionForeground(Color.WHITE);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Cabeceras con PRIMARY en vez del gris que no se veía
        t.getTableHeader().setBackground(UIConstants.PRIMARY);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.setGridColor(UIConstants.BORDER);
        t.setShowVerticalLines(false);
        return t;
    }

    /**
     * Construye (o reconstruye) el panel de botones en el idioma activo.
     * Se llama al iniciar y cada vez que el usuario pulsa el botón de idioma.
     * Al cambiar de idioma también se reconstruye el modelo de la tabla
     * para que las cabeceras cambien.
     */
    private void construirBotones() {
        panelBotones.removeAll();

        JButton btnCarrito    = btn(LanguageManager.getTexto("anadirCarrito"),  UIConstants.PRIMARY);
        JButton btnVerCarrito = btn(LanguageManager.getTexto("verCarrito"),     UIConstants.PRIMARY);
        JButton btnFavorito   = btn(LanguageManager.getTexto("favorito"),       UIConstants.PRIMARY);
        JButton btnPedidos    = btn(LanguageManager.getTexto("pedidos"),        UIConstants.PRIMARY);
        JButton btnPerfil     = btn(LanguageManager.getTexto("perfil"),         UIConstants.PRIMARY);
        JButton btnCerrar     = btn(LanguageManager.getTexto("cerrarSesion"),   UIConstants.SECONDARY);
        JButton btnIdioma     = btn(idiomaActivo.equals("es") ? "EN" : "ES",   UIConstants.BORDER);
        btnIdioma.setForeground(UIConstants.TEXT);
        JButton btnRefrescar  = btn(idiomaActivo.equals("es") ? " Refrescar" : " Refresh", new Color(40, 40, 60));
        btnRefrescar.setForeground(new Color(180, 180, 200));

        panelBotones.add(btnCarrito);
        panelBotones.add(btnVerCarrito);
        panelBotones.add(btnFavorito);
        panelBotones.add(btnPedidos);
        panelBotones.add(btnPerfil);

        // Botón admin solo para ADMIN
        if ("ADMIN".equals(usuario.getRol())) {
            JButton btnAdmin = btn(LanguageManager.getTexto("admin"), new Color(120, 60, 200));
            panelBotones.add(btnAdmin);
            btnAdmin.addActionListener(e -> {
                String pass = JOptionPane.showInputDialog(this,
                    LanguageManager.getTexto("contrasenaAdmin"));
                if (pass != null && pass.equals("admin123")) {
                    new VentanaAdminControl(usuario).setVisible(true);
                } else if (pass != null) {
                    Toast.show(this, LanguageManager.getTexto("contrasenaIncorrecta"));
                }
            });
        }

        panelBotones.add(btnCerrar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnIdioma);

        // Listeners
        btnCarrito.addActionListener(e    -> agregarAlCarrito());
        btnRefrescar.addActionListener(e  -> cargarTabla());
        btnVerCarrito.addActionListener(e -> {
            new VentanaCarrito(this, usuario).setVisible(true);
            cargarTabla(); // Actualizamos stock al volver
        });
        btnFavorito.addActionListener(e -> toggleFavorito());
        btnPedidos.addActionListener(e  -> new VentanaMisPedidos(usuario).setVisible(true));
        btnPerfil.addActionListener(e   -> new VentanaMiPerfil(usuario).setVisible(true));
        btnCerrar.addActionListener(e   -> { VentanaLogin.abrirLogin(); dispose(); });

        btnIdioma.addActionListener(e -> {
            idiomaActivo = idiomaActivo.equals("es") ? "en" : "es";
            LanguageManager.setIdioma(idiomaActivo);
            // Reconstruir modelo con nuevas cabeceras
            modelo = crearModelo();
            tabla.setModel(modelo);
            cargarTabla();
            construirBotones();
            panelBotones.revalidate();
            panelBotones.repaint();
        });

        panelBotones.revalidate();
        panelBotones.repaint();
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
     * Recarga el catálogo desde la BD usando un SwingWorker.
     *
     * La consulta a Supabase se ejecuta en un hilo de fondo (doInBackground)
     * para no bloquear el Event Dispatch Thread (EDT) de Swing durante la
     * espera de red. Mientras el hilo trabaja, la tabla muestra una fila de
     * "Cargando..." al usuario. Cuando el hilo termina, done() vuelca los
     * datos en la tabla desde el EDT, que es el único hilo seguro para
     * modificar componentes Swing.
     *
     * Este patrón cumple el requisito técnico del módulo de Servicios y
     * Procesos: uso real de hilos (Thread/Runnable) en Swing mediante la
     * clase SwingWorker, que internamente crea y gestiona un Thread del
     * pool de hilos de Swing.
     *
     * Se llama automáticamente al abrir, al volver del carrito y al cambiar
     * idioma.
     */
    private void cargarTabla() {
        // 1. Limpiamos la tabla y mostramos "Cargando..." mientras trabaja el hilo
    	modelo.addRow(new Object[]{
    		    "...",
    		    LanguageManager.getIdioma().equals("es")
    		        ? "Cargando productos..."
    		        : "Loading products...",
    		    0.0,
    		    0,
    		    false
    		});

        // 2. SwingWorker: hilo de fondo para la consulta a la BD
        SwingWorker<Object[][], Void> worker = new SwingWorker<>() {

            /**
             * doInBackground() se ejecuta en un Thread del pool de Swing,
             * FUERA del EDT. Aquí hacemos la consulta a Supabase sin bloquear
             * la interfaz gráfica.
             */
            @Override
            protected Object[][] doInBackground() {
                List<producto> productos = dao.obtenerTodos();
                Set<Integer>   favs      = dao.obtenerFavoritosIds(usuario.getId_usuario());

                Object[][] filas = new Object[productos.size()][5];
                for (int i = 0; i < productos.size(); i++) {
                    producto p = productos.get(i);
                    filas[i] = new Object[]{
                        p.getId_producto(),
                        p.getNombre_prod(),
                        p.getPrecio(),
                        p.getStock(),
                        favs.contains(p.getId_producto())
                    };
                }
                return filas;
            }

            /**
             * done() se ejecuta de vuelta en el EDT una vez que doInBackground()
             * termina. Aquí actualizamos la tabla de forma segura.
             */
            @Override
            protected void done() {
                try {
                    Object[][] filas = get();
                    modelo.setRowCount(0);
                    for (Object[] fila : filas) {
                        modelo.addRow(fila);
                    }
                } catch (Exception e) {
                    modelo.setRowCount(0);
                    System.out.println("Error al cargar productos: " + e.getMessage());
                    Toast.show(VentanaProductosUsuario.this,
                        LanguageManager.getIdioma().equals("es")
                            ? "Error al cargar productos."
                            : "Error loading products.");
                }
            }
        };

        // 3. Lanzamos el worker: crea un Thread interno y lo arranca
        worker.execute();
    }

    /**
     * Añade los productos seleccionados en la tabla al carrito.
     * Pide cantidad para cada uno y valida contra el stock disponible.
     */
    private void agregarAlCarrito() {
        int[] filas = tabla.getSelectedRows();
        if (filas.length == 0) {
            Toast.show(this, LanguageManager.getTexto("seleccionaProducto")); return;
        }
        for (int fila : filas) {
            // Saltamos la fila "Cargando..." si aún está presente
            Object idVal = modelo.getValueAt(fila, 0);
            if ("...".equals(idVal.toString())) continue;

            int idProducto = (int) idVal;
            String nombre  = modelo.getValueAt(fila, 1).toString();
            int stock      = (int) modelo.getValueAt(fila, 3);

            String s = JOptionPane.showInputDialog(this,
                nombre + " (stock: " + stock + "):", "1");
            try {
                int cantidad = Integer.parseInt(s);
                if (cantidad <= 0 || cantidad > stock) {
                    Toast.show(this, LanguageManager.getTexto("errorCantidad")); continue;
                }
                carritoDAO.agregarOActualizar(usuario.getId_usuario(), idProducto, cantidad);
                Toast.show(this, nombre + " " + LanguageManager.getTexto("añadidoCarrito"));
            } catch (NumberFormatException ex) {
                Toast.show(this, LanguageManager.getTexto("errorCantidad"));
            }
        }
        cargarTabla();
    }

    /**
     * Marca o desmarca como favorito el producto seleccionado.
     */
    private void toggleFavorito() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            Toast.show(this, LanguageManager.getTexto("seleccionaProducto")); return;
        }
        Object idVal = modelo.getValueAt(fila, 0);
        if ("...".equals(idVal.toString())) return; // Aún cargando

        int idProducto = (int)     idVal;
        boolean esFav  = (boolean) modelo.getValueAt(fila, 4);
        if (esFav) {
            dao.quitarFavorito(usuario.getId_usuario(), idProducto);
            Toast.show(this, LanguageManager.getTexto("favoritoEliminado"));
        } else {
            dao.marcarFavorito(usuario.getId_usuario(), idProducto);
            Toast.show(this, LanguageManager.getTexto("favoritoAñadido"));
        }
        cargarTabla();
    }
}