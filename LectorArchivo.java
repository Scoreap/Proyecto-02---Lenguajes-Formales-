package archivos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Lee archivos de texto del sistema.
 * Utilizado para cargar el archivo programa.txt.
 *
 * @author Sophia
 */
public class LectorArchivo {

    /**
     * Lee un archivo completo y retorna su contenido.
     *
     * @param rutaArchivo Ruta del archivo
     * @return Contenido del archivo
     * @throws IOException Si hay error al leer
     */
    public static String leerArchivo(String rutaArchivo) throws IOException {
        StringBuilder contenido = new StringBuilder();

        try (BufferedReader lector = new BufferedReader(
                new FileReader(rutaArchivo, StandardCharsets.UTF_8))) {

            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        }

        return contenido.toString();
    }

    /**
     * Verifica si un archivo existe y es legible.
     *
     * @param rutaArchivo Ruta del archivo
     * @return true si existe y es legible
     */
    public static boolean archivoExiste(String rutaArchivo) {
        try {
            BufferedReader lector = new BufferedReader(
                    new FileReader(rutaArchivo, StandardCharsets.UTF_8));
            lector.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
