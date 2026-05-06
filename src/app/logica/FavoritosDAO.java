package app.logica;

import java.sql.*;
import java.util.*;
import app.modelo.producto;
import app.bbdd.ConexionBD;

/**
 * FavoritosDAO — Acceso a datos para la tabla "favoritos".
 *
 * Tabla de relación muchos-a-muchos entre usuarios y productos.
 * Cada método obtiene la conexión con ConexionBD.getConexion() para evitar NPE.
 *
 * @author EatMe Team
 */
public class FavoritosDAO {

    /** Marca un producto como favorito. Si ya lo estaba, no hace nada. */
    public void marcarFavorito(int idUsuario, int idProducto) {
        String sql = "INSERT INTO favoritos (id_usuario, id_producto) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error marcarFavorito: " + e.getMessage());
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
            System.out.println("Error quitarFavorito: " + e.getMessage());
        }
    }

    /** Devuelve los IDs de productos favoritos del usuario. */
    public Set<Integer> obtenerFavoritosIds(int idUsuario) {
        Set<Integer> set = new HashSet<>();
        String sql = "SELECT id_producto FROM favoritos WHERE id_usuario = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) set.add(rs.getInt("id_producto"));
            }
        } catch (SQLException e) {
            System.out.println("Error obtenerFavoritosIds: " + e.getMessage());
        }
        return set;
    }

    /** Devuelve la lista completa de productos favoritos del usuario con todos sus datos. */
    public List<producto> obtenerFavoritosUsuario(int idUsuario) {
        List<producto> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_producto, p.nombre_prod, p.precio, p.stock
            FROM favoritos f
            JOIN productos p ON f.id_producto = p.id_producto
            WHERE f.id_usuario = ?
        """;
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new producto(
                        rs.getInt("id_producto"),
                        rs.getString("nombre_prod"),
                        rs.getDouble("precio"),
                        rs.getInt("stock")
                    ));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error obtenerFavoritosUsuario: " + e.getMessage());
        }
        return lista;
    }
}
