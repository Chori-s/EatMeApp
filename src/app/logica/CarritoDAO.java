package app.logica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import app.modelo.CarritoItem;
import app.bbdd.ConexionBD;

/**
 * CarritoDAO — Acceso a datos para la tabla "carrito".
 *
 * El carrito es persistente en BD: sobrevive al cierre de la app.
 * Si el usuario añade el mismo producto dos veces, se suma la cantidad.
 * Cada método obtiene la conexión con ConexionBD.getConexion() para evitar NPE.
 *
 * @author EatMe Team
 */
public class CarritoDAO {

    /** Devuelve todos los items del carrito de un usuario con nombre y precio del producto. */
    public List<CarritoItem> obtenerPorUsuario(int idUsuario) {
        List<CarritoItem> lista = new ArrayList<>();
        String sql = """
            SELECT c.id_carrito, c.id_producto, c.cantidad,
                   p.nombre_prod, p.precio
            FROM carrito c
            JOIN productos p ON c.id_producto = p.id_producto
            WHERE c.id_usuario = ?
        """;
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new CarritoItem(
                        rs.getInt("id_carrito"),
                        rs.getInt("id_producto"),
                        rs.getString("nombre_prod"),
                        rs.getDouble("precio"),
                        rs.getInt("cantidad")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obtener carrito: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Añade un producto al carrito. Si ya existe, suma la cantidad nueva a la existente
     * en vez de crear una fila duplicada.
     */
    public void agregarOActualizar(int idUsuario, int idProducto, int cantidad) {
        String check = "SELECT id_carrito, cantidad FROM carrito WHERE id_usuario=? AND id_producto=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(check)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    actualizarCantidad(rs.getInt("id_carrito"), rs.getInt("cantidad") + cantidad);
                } else {
                    String insert = "INSERT INTO carrito (id_usuario, id_producto, cantidad) VALUES (?,?,?)";
                    try (PreparedStatement ins = ConexionBD.getConexion().prepareStatement(insert)) {
                        ins.setInt(1, idUsuario);
                        ins.setInt(2, idProducto);
                        ins.setInt(3, cantidad);
                        ins.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error agregar/actualizar carrito: " + e.getMessage());
        }
    }

    /** Actualiza la cantidad de un item del carrito. */
    public void actualizarCantidad(int idCarrito, int nuevaCantidad) {
        String sql = "UPDATE carrito SET cantidad=? WHERE id_carrito=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setInt(2, idCarrito);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar cantidad carrito: " + e.getMessage());
        }
    }

    /** Elimina un item concreto del carrito por su ID. */
    public void eliminarPorId(int idCarrito) {
        String sql = "DELETE FROM carrito WHERE id_carrito=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idCarrito);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar item carrito: " + e.getMessage());
        }
    }

    /** Vacía todo el carrito del usuario. Se llama al procesar el pedido. */
    public void vaciarCarritoUsuario(int idUsuario) {
        String sql = "DELETE FROM carrito WHERE id_usuario=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error vaciar carrito: " + e.getMessage());
        }
    }
}
