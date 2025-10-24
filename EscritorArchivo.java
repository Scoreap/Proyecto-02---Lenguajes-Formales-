package archivos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Escribe archivos de texto en el sistema.
 * Utilizado para generar reportes y archivos DOT.
 *
 * @author Sophia
 */
public class EscritorArchivo {

    /**
     * Escribe contenido en un archivo.
     *
     * @param rutaArchivo Ruta del archivo de salida
     * @param contenido Contenido a escribir
     * @throws IOException Si hay error al escribir
     */
    public static void escribirArchivo(String rutaArchivo, String contenido) throws IOException {
        try (BufferedWriter escritor = new BufferedWriter(
                new FileWriter(rutaArchivo, StandardCharsets.UTF_8))) {
            escritor.write(contenido);
        }
    }

    /**
     * Escribe contenido en un archivo y muestra mensaje de confirmación.
     *
     * @param rutaArchivo Ruta del archivo de salida
     * @param contenido Contenido a escribir
     * @param nombreArchivo Nombre descriptivo del archivo
     */
    public static void escribirArchivoConMensaje(String rutaArchivo, String contenido, String nombreArchivo) {
        try {
            escribirArchivo(rutaArchivo, contenido);
            System.out.println("Archivo generado: " + nombreArchivo + " -> " + rutaArchivo);
        } catch (IOException e) {
            System.err.println("Error al generar " + nombreArchivo + ": " + e.getMessage());
        }
    }
}
