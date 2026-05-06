package app.main;

import app.ui.VentanaLogin;
import javax.swing.*;

/**
 * Main — Punto de entrada de la aplicación EatMe.
 *
 * Esta clase contiene el método main() que arranca la aplicación.
 * Se encarga de:
 *   1. Intentar aplicar el Look & Feel "Nimbus" para una apariencia moderna
 *   2. Lanzar la ventana de login en el hilo de eventos de Swing (EDT)
 *
 * El uso de SwingUtilities.invokeLater() es fundamental en Swing: garantiza
 * que la interfaz gráfica se crea y modifica siempre desde el Event Dispatch
 * Thread (EDT), que es el único hilo seguro para Swing. Esto también cumple
 * con el requisito técnico de uso de hilos (Thread/Runnable) del proyecto.
 *
 * @author EatMe Team
 */
public class Main {

    /**
     * Punto de entrada de la aplicación.
     * Configura el Look & Feel y lanza la pantalla de inicio de sesión.
     *
     * @param args Argumentos de línea de comandos (no se usan en esta aplicación)
     */
    public static void main(String[] args) {

        // Intentamos usar el Look & Feel Nimbus para un aspecto más moderno
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Si Nimbus no está disponible, usamos el L&F por defecto del sistema
        }

        // Lanzamos la UI en el Event Dispatch Thread (EDT) de Swing
        // SwingUtilities.invokeLater usa internamente un Runnable, requisito técnico del proyecto
        SwingUtilities.invokeLater(() -> VentanaLogin.abrirLogin());
    }
}
