package app.modelo;

/**
 * Usuario — Clase modelo que representa un usuario registrado en EatMe.
 *
 * Almacena los datos de la cuenta del usuario: identificador, nombre, email,
 * contraseña y rol. El rol determina qué pantallas puede ver:
 *   - "USER"  → accede al catálogo, carrito, pedidos y perfil
 *   - "ADMIN" → además puede acceder al panel de administración
 *
 * La clase incluye varios constructores para los distintos contextos en los
 * que se crea un Usuario (login completo, registro, consulta admin...).
 *
 * @author EatMe Team
 */
public class Usuario {

    private int id_usuario;
    private String nombre;
    private String email;
    private String contrasena;
    private String fotoPath; // Campo reservado, actualmente no se usa en la BD
    private String rol;

    /**
     * Constructor completo con rol. Se usa al hacer login para recuperar
     * todos los datos del usuario incluyendo su rol desde la BD.
     *
     * @param id_usuario  ID único del usuario
     * @param nombre      Nombre del usuario
     * @param email       Email de la cuenta
     * @param contrasena  Contraseña de la cuenta
     * @param rol         Rol del usuario ("USER" o "ADMIN")
     */
    public Usuario(int id_usuario, String nombre, String email, String contrasena, String rol) {
        this.id_usuario = id_usuario;
        this.nombre     = nombre;
        this.email      = email;
        this.contrasena = contrasena;
        this.rol        = rol;
    }

    /**
     * Constructor para registrar un nuevo usuario (sin ID ni rol).
     * El ID lo genera la BD automáticamente y el rol por defecto es "USER".
     *
     * @param nombre     Nombre del nuevo usuario
     * @param email      Email del nuevo usuario
     * @param contrasena Contraseña del nuevo usuario
     */
    public Usuario(String nombre, String email, String contrasena) {
        this.nombre     = nombre;
        this.email      = email;
        this.contrasena = contrasena;
    }

    /**
     * Constructor con ID pero sin rol. Se usa en consultas del panel admin
     * donde el rol no es relevante para la vista de usuarios.
     * Asigna "USER" por defecto para evitar NullPointerException.
     *
     * @param id_usuario  ID del usuario
     * @param nombre      Nombre del usuario
     * @param email       Email del usuario
     * @param contrasena  Contraseña del usuario
     */
    public Usuario(int id_usuario, String nombre, String email, String contrasena) {
        this.id_usuario = id_usuario;
        this.nombre     = nombre;
        this.email      = email;
        this.contrasena = contrasena;
        this.rol        = "USER"; // Rol por defecto cuando no se especifica
    }

    // Getters y setters
    public int getId_usuario()                   { return id_usuario; }
    public void setId_usuario(int id_usuario)    { this.id_usuario = id_usuario; }

    public String getNombre()                    { return nombre; }
    public void setNombre(String nombre)         { this.nombre = nombre; }

    public String getEmail()                     { return email; }
    public void setEmail(String email)           { this.email = email; }

    public String getContrasena()                { return contrasena; }
    public void setContrasena(String c)          { this.contrasena = c; }

    public String getFotoPath()                  { return fotoPath; }
    public void setFotoPath(String fotoPath)     { this.fotoPath = fotoPath; }

    public String getRol()                       { return rol; }

    /**
     * Representación en texto del usuario para depuración y logs.
     *
     * @return Cadena con el formato: id | nombre | email
     */
    @Override
    public String toString() {
        return id_usuario + " | " + nombre + " | " + email;
    }
}
