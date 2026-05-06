package app.utils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.PropertyResourceBundle;

/**
 * LanguageManager — Gestor de internacionalización (i18n) de EatMe.
 *
 * Carga los textos desde archivos .properties en UTF-8 para soportar
 * correctamente la ñ, acentos y caracteres especiales del español.
 *
 * Los archivos deben estar en src/lang/ (source folder de Eclipse):
 *   - src/lang/messages_es.properties  → español
 *   - src/lang/messages_en.properties  → inglés
 *
 * Eclipse los copia automáticamente a bin/lang/ al compilar, por lo que
 * en tiempo de ejecución están disponibles en el classpath como
 * "lang/messages_es.properties".
 *
 * Uso:
 *   LanguageManager.getTexto("login")   → "Iniciar sesión" / "Login"
 *   LanguageManager.setIdioma("en")     → cambia a inglés
 *   LanguageManager.getIdioma()         → "es" o "en"
 *
 * @author EatMe Team
 */
public class LanguageManager {

    /** Idioma activo en toda la aplicación: "es" (por defecto) o "en". */
    private static String idiomaActivo = "es";

    /** Bundle cargado en memoria para el idioma activo. Se recarga al cambiar de idioma. */
    private static PropertyResourceBundle bundle = null;

    /**
     * Cambia el idioma activo. El bundle se recarga la próxima vez que se llame a getTexto().
     *
     * @param idioma Código ISO 639-1: "es" o "en"
     */
    public static void setIdioma(String idioma) {
        idiomaActivo = idioma;
        bundle = null; // Forzamos recarga del bundle en el siguiente getTexto()
    }

    /**
     * Devuelve el idioma activo actualmente.
     *
     * @return "es" o "en"
     */
    public static String getIdioma() {
        return idiomaActivo;
    }

    /**
     * Devuelve el texto de una clave en el idioma activo.
     *
     * Carga el .properties en UTF-8 para soportar ñ y acentos correctamente.
     * El archivo se busca en el classpath como "lang/messages_{idioma}.properties".
     * Eclipse copia los .properties de src/lang/ a bin/lang/ al compilar.
     *
     * Si la clave no existe devuelve la propia clave (sin corchetes) para que
     * al menos se muestre algo legible en vez de texto roto.
     *
     * @param clave La clave del texto (ej: "login", "carrito", "errorCorreo")
     * @return El texto traducido, o la clave si no se encuentra
     */
    public static String getTexto(String clave) {
        // Cargamos el bundle si es null (primera vez o tras cambio de idioma)
        if (bundle == null) {
            cargarBundle();
        }

        try {
            return bundle.getString(clave);
        } catch (Exception e) {
            // Si la clave no existe, devolvemos la clave misma para detectar el problema
            System.out.println("LanguageManager: clave no encontrada → " + clave);
            return clave;
        }
    }

    /**
     * Carga el archivo .properties del idioma activo desde el classpath en UTF-8.
     * Si falla la carga, el bundle queda como el español por defecto.
     */
    private static void cargarBundle() {
        // Ruta relativa al classpath: Eclipse copia src/lang/*.properties → bin/lang/
        String ruta = "lang/messages_" + idiomaActivo + ".properties";
        try {
            InputStream stream = LanguageManager.class.getClassLoader().getResourceAsStream(ruta);
            if (stream == null) {
                System.out.println("LanguageManager: no se encontró el archivo → " + ruta);
                // Fallback: intentar con el español
                if (!idiomaActivo.equals("es")) {
                    String fallback = "lang/messages_es.properties";
                    stream = LanguageManager.class.getClassLoader().getResourceAsStream(fallback);
                }
            }
            if (stream != null) {
                bundle = new PropertyResourceBundle(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));
                System.out.println("LanguageManager: idioma cargado → " + idiomaActivo);
            } else {
                System.out.println("LanguageManager ERROR: no se pudo cargar ningún properties.");
                bundle = new PropertyResourceBundle(
                    new InputStreamReader(
                        LanguageManager.class.getClassLoader()
                            .getResourceAsStream("lang/messages_es.properties"),
                        StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            System.out.println("LanguageManager ERROR al cargar bundle: " + e.getMessage());
        }
    }
}
