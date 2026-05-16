package app.logica;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import app.modelo.producto;
import app.bbdd.ConexionBD;

/**
 * ProductoDAO — Acceso a datos para las tablas "productos" y "favoritos".
 *
 * Cada método obtiene la conexión en el momento de ejecutarse con
 * ConexionBD.getConexion() para evitar NPE por conexión nula en el constructor.
 *
 * @author Iván
 */
public class ProductoDAO {

    /** Inserta un nuevo producto en el catálogo. */
    public void insertar(producto p) {
        String sql = "INSERT INTO productos (nombre_prod, precio, stock) VALUES (?,?,?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, p.getNombre_prod());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getStock());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertar producto: " + e.getMessage());
        }
    }

    /** Devuelve todos los productos del catálogo. */
    public List<producto> obtenerTodos() {
        List<producto> lista = new ArrayList<>();
        String sql = "SELECT * FROM productos";
        try (Statement st = ConexionBD.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre_prod"),
                    rs.getDouble("precio"),
                    rs.getInt("stock")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error obtener productos: " + e.getMessage());
        }
        return lista;
    }

    /** Busca un producto por su ID. Devuelve null si no existe. */
    public producto obtenerPorId(int id) {
        String sql = "SELECT * FROM productos WHERE id_producto=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_prod"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obtener producto por id: " + e.getMessage());
        }
        return null;
    }

    /** Actualiza el stock de un producto tras procesar un pedido. */
    public void actualizarStock(int idProducto, int nuevoStock) {
        String sql = "UPDATE productos SET stock=? WHERE id_producto=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar stock: " + e.getMessage());
        }
    }

    /** Elimina un producto del catálogo por su ID. */
    public void eliminar(int idProducto) {
        String sql = "DELETE FROM productos WHERE id_producto=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar producto: " + e.getMessage());
        }
    }

    /** Marca un producto como favorito. Ignora si ya estaba marcado. */
    public void marcarFavorito(int idUsuario, int idProducto) {
        String sql = "INSERT INTO favoritos (id_usuario, id_producto) VALUES (?,?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error marcar favorito: " + e.getMessage());
        }
    }

    /** Quita un producto de favoritos. */
    public void quitarFavorito(int idUsuario, int idProducto) {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_producto = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error quitar favorito: " + e.getMessage());
        }
    }

    /** Devuelve el Set de IDs de productos favoritos del usuario (para pintar checkboxes). */
    public Set<Integer> obtenerFavoritosIds(int idUsuario) {
        Set<Integer> ids = new HashSet<>();
        String sql = "SELECT id_producto FROM favoritos WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("id_producto"));
            }
        } catch (SQLException e) {
            System.out.println("Error obtener favoritos ids: " + e.getMessage());
        }
        return ids;
    }
}
