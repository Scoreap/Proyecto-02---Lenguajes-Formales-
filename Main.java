package app;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Clase principal del proyecto.
 * Punto de entrada del programa.
 *
 * @author Sophia
 */
public class Main {

    public static void main(String[] args) {
        // Configurar codificación UTF-8
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Error al configurar UTF-8: " + e.getMessage());
        }

        // Ruta del archivo de entrada
        String rutaArchivo = "programa.txt";

        // Si se proporciona argumento, usarlo como ruta
        if (args.length > 0) {
            rutaArchivo = args[0];
        }

        // Ejecutar controlador principal
        ControladorPrincipal controlador = new ControladorPrincipal(rutaArchivo);
        controlador.ejecutar();
    }
}
