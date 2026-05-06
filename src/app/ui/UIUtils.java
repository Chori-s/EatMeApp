package app.ui;

import java.awt.*;
import javax.swing.*;

/**
 * UIUtils — Clase de utilidades para la interfaz gráfica de EatMe.
 *
 * Contiene métodos estáticos reutilizables para aplicar efectos visuales
 * a las ventanas de la aplicación. Actualmente implementa un efecto de
 * fade-in (aparición gradual) para hacer las transiciones entre ventanas
 * más suaves y agradables para el usuario.
 *
 * @author EatMe Team
 */
public class UIUtils {

    /**
     * Aplica un efecto de aparición gradual (fade-in) a cualquier ventana Swing.
     *
     * Incrementa la opacidad de la ventana de 0 a 1 en pasos pequeños con una
     * pequeña pausa entre cada paso para crear el efecto de animación.
     * Si el sistema no soporta transparencia de ventanas (algunos entornos Linux
     * o configuraciones de escritorio), simplemente muestra la ventana sin animación.
     *
     * Nota: Este método muestra la ventana internamente (setVisible(true)),
     * así que no es necesario llamarlo externamente después.
     *
     * @param window La ventana (JFrame, JDialog o cualquier Window) a mostrar con fade-in
     */
    public static void fadeInWindow(Window window) {
        try {
            // Comprobamos si el entorno gráfico soporta transparencia de ventanas
            boolean soportaTransparencia = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);

            if (soportaTransparencia) {
                window.setOpacity(0f); // Empezamos completamente transparente
                window.setVisible(true);

                // Subimos la opacidad poco a poco para crear el efecto fade-in
                for (float i = 0f; i <= 1f; i += 0.05f) {
                    window.setOpacity(i);
                    try {
                        Thread.sleep(15); // Pausa de 15ms entre cada paso (~60fps)
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }

                window.setOpacity(1f); // Nos aseguramos de que quede al 100%
                return;
            }
        } catch (Exception e) {
            System.out.println("FadeIn no soportado, mostrando ventana sin animacion.");
        }

        // Fallback: si no hay soporte de transparencia, mostramos directamente
        window.setVisible(true);
    }
}
