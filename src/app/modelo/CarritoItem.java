package app.modelo;

/**
 * CarritoItem — Clase modelo que representa un item del carrito de compra.
 *
 * Combina los datos del registro de la tabla "carrito" con los datos del
 * producto correspondiente (nombre y precio), que se obtienen mediante JOIN
 * en CarritoDAO. De esta forma la vista tiene todo lo necesario sin hacer
 * consultas adicionales.
 *
 * @author EatMe Team
 */
public class CarritoItem {

    private int id_carrito;       // ID del registro en la tabla carrito
    private int id_producto;      // ID del producto asociado
    private String nombreProducto; // Nombre del producto (viene del JOIN con productos)
    private double precioUnitario; // Precio por unidad en euros
    private int cantidad;          // Número de unidades en el carrito

    /**
     * Constructor completo para crear un item del carrito con todos sus datos.
     *
     * @param id_carrito    ID del registro en la tabla carrito
     * @param id_producto   ID del producto
     * @param nombreProducto Nombre del producto
     * @param precioUnitario Precio por unidad
     * @param cantidad      Cantidad en el carrito
     */
    public CarritoItem(int id_carrito, int id_producto, String nombreProducto,
                       double precioUnitario, int cantidad) {
        this.id_carrito     = id_carrito;
        this.id_producto    = id_producto;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad       = cantidad;
    }

    // Getters
    public int getId_carrito()          { return id_carrito; }
    public int getId_producto()         { return id_producto; }
    public String getNombreProducto()   { return nombreProducto; }
    public double getPrecioUnitario()   { return precioUnitario; }
    public int getCantidad()            { return cantidad; }

    /**
     * Calcula el subtotal de este item multiplicando el precio por la cantidad.
     *
     * @return El subtotal en euros (precio * cantidad)
     */
    public double getSubtotal() {
        return precioUnitario * cantidad;
    }
}
