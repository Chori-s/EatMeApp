package app.modelo;

import java.sql.Timestamp;

/**
 * Pedido — Clase modelo que representa un pedido realizado en EatMe.
 *
 * Cada pedido corresponde a un producto que un usuario ha comprado. Cuando
 * el usuario procesa su carrito, cada item del carrito genera un Pedido
 * independiente en la base de datos. Esta clase incluye tanto los datos
 * propios del pedido (usuario, producto, cantidad, fecha) como datos del
 * producto obtenidos por JOIN (nombre y precio), necesarios para mostrar
 * el historial y generar el ticket.
 *
 * @author EatMe Team
 */
public class Pedido {

    private int id_pedido;
    private int id_usuario;
    private int id_producto;
    private int cantidad;
    private Timestamp fecha;
    private String nombreProducto; // Nombre del producto (del JOIN con productos)
    private double precio;          // Precio unitario en el momento del pedido

    /**
     * Constructor completo para cargar un pedido desde la base de datos
     * con todos sus datos, incluidos los del producto asociado.
     *
     * @param id_pedido       ID del pedido
     * @param id_usuario      ID del usuario que hizo el pedido
     * @param id_producto     ID del producto pedido
     * @param cantidad        Cantidad de unidades pedidas
     * @param fecha           Fecha y hora en que se realizó el pedido
     * @param nombreProducto  Nombre del producto (obtenido por JOIN)
     * @param precio          Precio unitario del producto en el momento del pedido
     */
    public Pedido(int id_pedido, int id_usuario, int id_producto, int cantidad,
                  Timestamp fecha, String nombreProducto, double precio) {
        this.id_pedido      = id_pedido;
        this.id_usuario     = id_usuario;
        this.id_producto    = id_producto;
        this.cantidad       = cantidad;
        this.fecha          = fecha;
        this.nombreProducto = nombreProducto;
        this.precio         = precio;
    }

    /**
     * Constructor básico para insertar un nuevo pedido en la BD.
     * Solo necesita los campos obligatorios; la fecha la genera automáticamente la BD.
     *
     * @param id_usuario  ID del usuario que realiza el pedido
     * @param id_producto ID del producto pedido
     * @param cantidad    Cantidad de unidades
     */
    public Pedido(int id_usuario, int id_producto, int cantidad) {
        this.id_usuario  = id_usuario;
        this.id_producto = id_producto;
        this.cantidad    = cantidad;
    }

    // Getters y setters
    public int getId_pedido()                    { return id_pedido; }
    public void setId_pedido(int id_pedido)      { this.id_pedido = id_pedido; }

    public int getId_usuario()                   { return id_usuario; }
    public void setId_usuario(int id_usuario)    { this.id_usuario = id_usuario; }

    public int getId_producto()                  { return id_producto; }
    public void setId_producto(int id_producto)  { this.id_producto = id_producto; }

    public int getCantidad()                     { return cantidad; }
    public void setCantidad(int cantidad)        { this.cantidad = cantidad; }

    public Timestamp getFecha()                  { return fecha; }
    public void setFecha(Timestamp fecha)        { this.fecha = fecha; }

    public String getNombreProducto()            { return nombreProducto; }
    public void setNombreProducto(String n)      { this.nombreProducto = n; }

    public double getPrecio()                    { return precio; }
    public void setPrecio(double precio)         { this.precio = precio; }

    /**
     * Genera la representación en formato ticket del pedido.
     * Se muestra en VentanaMisPedidos cuando el usuario pulsa "Ver Ticket".
     *
     * @return Cadena formateada en estilo recibo de caja
     */
    @Override
    public String toString() {
        return  "──────────────────────────\n" +
                " Pedido #" + id_pedido + "\n" +
                " Fecha:    " + fecha + "\n" +
                " Producto: " + nombreProducto + "\n" +
                " Cantidad: " + cantidad + " uds.\n" +
                " Precio:   " + String.format("%.2f", precio) + " €/ud.\n" +
                " TOTAL:    " + String.format("%.2f", precio * cantidad) + " €\n" +
                "──────────────────────────";
    }
}
