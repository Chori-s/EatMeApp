package app.ui;

import java.awt.*;

/**
 * UIConstants — Paleta de colores y constantes de diseño de EatMe.
 *
 * Define los colores globales de la aplicación siguiendo un tema oscuro moderno
 * con acentos en violeta/morado, muy usado en apps actuales de delivery y food tech.
 * Cualquier cambio de diseño solo requiere editar esta clase.
 *
 * @author EatMe Team
 */
public class UIConstants {

    /** Fondo principal: casi negro azulado */
    public static final Color BG        = new Color(15, 15, 25);

    /** Fondo de tarjetas, tablas y paneles secundarios */
    public static final Color CARD      = new Color(28, 28, 45);

    /** Color de texto principal: blanco suave */
    public static final Color TEXT      = new Color(235, 235, 245);

    /** Color primario de botones y acentos: violeta brillante */
    public static final Color PRIMARY   = new Color(108, 92, 231);

    /** Color secundario: coral rosado (botones de cancelar/volver) */
    public static final Color SECONDARY = new Color(253, 121, 168);

    /** Color de éxito: verde menta */
    public static final Color SUCCESS   = new Color(0, 210, 145);

    /** Color de peligro/error: rojo suave */
    public static final Color DANGER    = new Color(255, 85, 85);

    /** Color de selección de filas en tablas */
    public static final Color SELECT    = new Color(108, 92, 231, 180);

    /** Color de borde y separadores */
    public static final Color BORDER    = new Color(45, 45, 65);
}
