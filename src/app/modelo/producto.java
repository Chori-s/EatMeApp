package app.modelo;

/**
 * producto — Clase modelo que representa un producto del catálogo de EatMe.
 *
 * Contiene los datos básicos de un producto: su identificador, nombre,
 * precio y cantidad en stock. Las instancias de esta clase se crean en
 * los DAOs al leer los datos de la base de datos y se usan en las vistas
 * para mostrar el catálogo.
 *
 * Nota: el nombre de la clase está en minúscula porque así se creó al
 * inicio del proyecto. En Java la convención es PascalCase para clases,
 * pero lo mantenemos así para no romper las referencias existentes.
 *
 * @author EatMe Team
 */
public class producto {

    private int id_producto;
    private String nombre_prod;
    private double precio;
    private int stock;

    /**
     * Constructor completo para crear un producto con todos sus datos.
     *
     * @param id_producto  ID único del producto (generado por la BD)
     * @param nombre_prod  Nombre del producto
     * @param precio       Precio unitario del producto en euros
     * @param stock        Cantidad disponible en stock
     */
    public producto(int id_producto, String nombre_prod, double precio, int stock) {
        this.id_producto = id_producto;
        this.nombre_prod = nombre_prod;
        this.precio      = precio;
        this.stock       = stock;
    }

    // Getters y setters
    public int getId_producto()              { return id_producto; }
    public void setId_producto(int id)       { this.id_producto = id; }

    public String getNombre_prod()           { return nombre_prod; }
    public void setNombre_prod(String n)     { this.nombre_prod = n; }

    public double getPrecio()                { return precio; }
    public void setPrecio(double precio)     { this.precio = precio; }

    public int getStock()                    { return stock; }
    public void setStock(int stock)          { this.stock = stock; }

    /**
     * Representación en texto del producto para depuración y logs.
     *
     * @return Cadena con el formato: id | nombre | precio | stock
     */
    @Override
    public String toString() {
        return id_producto + " | " + nombre_prod + " | " + precio + " | " + stock;
    }
}
