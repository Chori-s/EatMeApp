package app.logica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import app.modelo.Pedido;
import app.bbdd.ConexionBD;

/**
 * PedidoDAO — Acceso a datos para la tabla "pedidos".
 *
 * Cada método obtiene la conexión con ConexionBD.getConexion() para evitar NPE.
 *
 * Los pedidos de una misma sesión de compra (mismo procesado de carrito) comparten
 * el mismo timestamp, de forma que en VentanaMisPedidos se pueden agrupar y mostrar
 * un ticket conjunto por sesión de compra, en vez de un ticket por producto.
 *
 * @author Iván
 */
public class PedidoDAO {

    /**
     * Inserta un pedido con un timestamp dado (para agrupar sesiones de compra).
     * La BD no genera la fecha automáticamente: se pasa explícitamente para que
     * todos los ítems del mismo carrito compartan el mismo instante.
     *
     * @param p         El pedido a insertar
     * @param timestamp El timestamp de la sesión de compra (compartido por todos los ítems)
     */
    public void insertarConFecha(Pedido p, Timestamp timestamp) {
        String sql = "INSERT INTO pedidos (id_usuario, id_producto, cantidad, fecha) VALUES (?,?,?,?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, p.getId_usuario());
            ps.setInt(2, p.getId_producto());
            ps.setInt(3, p.getCantidad());
            ps.setTimestamp(4, timestamp);
            ps.executeUpdate();
        } catch (SQLException e) {
            // Fallback: insertar sin fecha (la BD usará NOW())
            insertar(p);
            System.out.println("Fallback a INSERT sin fecha: " + e.getMessage());
        }
    }

    /**
     * Inserta un pedido dejando que la BD genere la fecha con NOW().
     * Usar solo cuando no se necesite agrupar por sesión.
     *
     * @param p El pedido a insertar
     */
    public void insertar(Pedido p) {
        String sql = "INSERT INTO pedidos (id_usuario, id_producto, cantidad) VALUES (?,?,?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, p.getId_usuario());
            ps.setInt(2, p.getId_producto());
            ps.setInt(3, p.getCantidad());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertar pedido: " + e.getMessage());
        }
    }

    /**
     * Devuelve el historial de pedidos de un usuario, del más reciente al más antiguo.
     * Hace JOIN con productos para obtener nombre y precio.
     *
     * @param idUsuario El ID del usuario
     * @return Lista de pedidos del usuario
     */
    public List<Pedido> obtenerPorUsuario(int idUsuario) {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.nombre_prod, pr.precio
            FROM pedidos p
            JOIN productos pr ON p.id_producto = pr.id_producto
            WHERE p.id_usuario = ?
            ORDER BY p.fecha DESC, p.id_pedido DESC
        """;
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getInt("id_usuario"),
                        rs.getInt("id_producto"),
                        rs.getInt("cantidad"),
                        rs.getTimestamp("fecha"),
                        rs.getString("nombre_prod"),
                        rs.getDouble("precio")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obtener pedidos usuario: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Devuelve todos los pedidos de todos los usuarios (para el panel admin).
     *
     * @return Lista con todos los pedidos
     */
    public List<Pedido> obtenerTodos() {
        List<Pedido> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, pr.nombre_prod, pr.precio
            FROM pedidos p
            JOIN productos pr ON p.id_producto = pr.id_producto
            ORDER BY p.fecha DESC, p.id_pedido DESC
        """;
        try (Statement st = ConexionBD.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Pedido(
                    rs.getInt("id_pedido"),
                    rs.getInt("id_usuario"),
                    rs.getInt("id_producto"),
                    rs.getInt("cantidad"),
                    rs.getTimestamp("fecha"),
                    rs.getString("nombre_prod"),
                    rs.getDouble("precio")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error obtener todos los pedidos: " + e.getMessage());
        }
        return lista;
    }
}
