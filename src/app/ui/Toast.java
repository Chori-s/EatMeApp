package app.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Toast — Notificación visual temporal al estilo Android.
 *
 * Aparece unos segundos en la parte superior de la ventana y desaparece sola.
 * Mucho menos intrusivo que un JOptionPane.
 *
 * IMPORTANTE: nunca llamar desde el constructor de una ventana antes de que
 * esta sea visible (getLocationOnScreen lanzaría IllegalComponentStateException).
 * Usar SwingUtilities.invokeLater() si es necesario mostrar un Toast en la carga.
 *
 * @author EatMe Team
 */
public class Toast {

    /**
     * Muestra un Toast sobre un JFrame.
     *
     * @param owner   El JFrame padre
     * @param message El mensaje a mostrar
     */
    public static void show(JFrame owner, String message) {
        JWindow w = new JWindow(owner);
        w.setBackground(new Color(0, 0, 0, 0));

        JLabel label = new JLabel("  " + message + "  ");
        label.setOpaque(true);
        label.setBackground(UIConstants.PRIMARY);
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));

        w.getContentPane().add(label);
        w.pack();

        Point p = owner.getLocationOnScreen();
        w.setLocation(p.x + owner.getWidth() / 2 - w.getWidth() / 2, p.y + 50);
        w.setVisible(true);

        new javax.swing.Timer(1800, e -> w.dispose()).start();
    }

    /**
     * Overload que acepta cualquier Component. Si no es un JFrame usa JOptionPane.
     *
     * @param owner   El componente padre
     * @param message El mensaje a mostrar
     */
    public static void show(Component owner, String message) {
        if (owner instanceof JFrame) {
            show((JFrame) owner, message);
        } else {
            JOptionPane.showMessageDialog(null, message);
        }
    }
}
