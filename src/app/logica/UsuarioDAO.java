package app.logica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import app.modelo.Usuario;
import app.bbdd.ConexionBD;

/**
 * UsuarioDAO — Acceso a datos para la tabla "usuarios".
 *
 * Cada método obtiene la conexión en el momento de ejecutarse llamando a
 * ConexionBD.getConexion(). Esto evita el NullPointerException que ocurría
 * cuando la conexión fallaba en el constructor y quedaba guardada como null.
 *
 * @author EatMe Team
 */
public class UsuarioDAO {

    /**
     * Registra un nuevo usuario. Devuelve false si el email ya existe o hay error.
     */
    public boolean registrar(String nombre, String email, String contrasena) {
        if (emailExists(email)) return false;
        String sql = "INSERT INTO usuarios (nombre, email, contrasena) VALUES (?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, contrasena);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /** Comprueba si un email ya está registrado. */
    private boolean emailExists(String email) {
        String sql = "SELECT id_usuario FROM usuarios WHERE email = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return true; // En caso de error asumimos que existe, para evitar duplicados
        }
    }

    /**
     * Intenta hacer login. Devuelve el Usuario completo con su rol, o null si falla.
     */
    public Usuario login(String email, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE email = ? AND contrasena = ?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id_usuario"),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("contrasena"),
                        rs.getString("rol")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error login: " + e.getMessage());
        }
        return null;
    }

    /** Devuelve todos los usuarios (para el panel admin). */
    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Statement st = ConexionBD.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Usuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("contrasena")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Error obtener usuarios: " + e.getMessage());
        }
        return lista;
    }

    /** Actualiza nombre y contraseña de un usuario (el email no cambia). */
    public void actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre=?, email=?, contrasena=? WHERE id_usuario=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getContrasena());
            ps.setInt(4, u.getId_usuario());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error actualizar usuario: " + e.getMessage());
        }
    }

    /** Inserta un nuevo usuario en la BD a partir de un objeto Usuario (uso interno del admin). */
    public void insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, email, contrasena) VALUES (?, ?, ?)";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getContrasena());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error insertar usuario: " + e.getMessage());
        }
    }

    /** Elimina un usuario por su ID. */
    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario=?";
        try (PreparedStatement ps = ConexionBD.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error eliminar usuario: " + e.getMessage());
        }
    }
}
