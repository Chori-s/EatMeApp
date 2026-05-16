package app.bbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * ConexionBD — Singleton que gestiona la conexión a la base de datos.
 *
 * Implementa el patrón Singleton para que toda la aplicación comparta
 * UNA SOLA conexión a Supabase en vez de abrir una nueva por cada DAO.
 * Esto evita el error NullPointerException que ocurría cuando la conexión
 * fallaba silenciosamente en el constructor de cada DAO.
 *
 * El método getConexion() reconecta automáticamente si la conexión está
 * cerrada o es nula, y lanza una RuntimeException clara si no puede
 * conectar (en vez de devolver null y petar más tarde con un NPE críptico).
 *
 * @author Iván
 */
public class ConexionBD {

    private static final String HOST = "aws-1-eu-central-2.pooler.supabase.com";
    private static final String PORT = "6543";
    private static final String DB   = "postgres";
    private static final String USER = "postgres.zufhkpnxpnuahtyylmsk";
    private static final String PASS = "Sev4b574vbt_";

    private static final String URL =
        "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB + "?sslmode=require";

    // Única instancia de conexión compartida por toda la app
    private static Connection instancia = null;

    /**
     * Devuelve la conexión activa a la base de datos.
     * Si no existe o está cerrada, la crea de nuevo.
     * Si no puede conectar, lanza RuntimeException con un mensaje claro.
     *
     * @return Connection activa y lista para usar (nunca null)
     * @throws RuntimeException si no se puede establecer la conexión
     */
    public static Connection getConexion() {
        try {
            // Si la conexión no existe o está cerrada, la creamos de nuevo
            if (instancia == null || instancia.isClosed()) {
                Class.forName("org.postgresql.Driver");
                instancia = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conectado con exito al Pooler de Supabase.");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Driver PostgreSQL no encontrado. Asegúrate de tener el .jar en el classpath.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "No se pudo conectar a la base de datos: " + e.getMessage(), e);
        }
        return instancia;
    }

    /**
     * Método de compatibilidad — llama a getConexion().
     * Mantenido para no romper código externo que use conectar().
     *
     * @return Connection activa (nunca null)
     */
    public static Connection conectar() {
        return getConexion();
    }
}
