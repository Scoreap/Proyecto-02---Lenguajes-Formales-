package app;

import analizadorLexico.*;
import analizadorSintactico.*;
import archivos.*;
import generadores.*;
import java.util.List;

/**
 * Controlador principal del analizador sintáctico.
 * Coordina todos los módulos del proyecto.
 *
 * @author Sophia
 */
public class ControladorPrincipal {

    private String rutaArchivo;
    private AnalizadorLexico analizadorLexico;
    private AnalizadorSintactico analizadorSintactico;
    private ClasificadorSemantico clasificadorSemantico;

    //region CONSTRUCTOR

    /**
     * Constructor del controlador.
     *
     * @param rutaArchivo Ruta del archivo programa.txt
     */
    public ControladorPrincipal(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    //endregion

    //region EJECUCIÓN PRINCIPAL

    /**
     * Ejecuta el análisis completo del programa.
     */
    public void ejecutar() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   ANALIZADOR SINTÁCTICO AVANZADO PARA SUBCONJUNTO JAVA     ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        try {
            // Paso 1: Leer archivo
            System.out.println("📄 Paso 1: Cargando archivo...");
            String codigoFuente = LectorArchivo.leerArchivo(rutaArchivo);
            System.out.println("Archivo cargado exitosamente\n");

            // Paso 2: Análisis léxico
            System.out.println("🔍 Paso 2: Análisis léxico...");
            analizadorLexico = new AnalizadorLexico(codigoFuente);
            analizadorLexico.analizar();

            // Paso 3: Análisis sintáctico
            System.out.println("🔍 Paso 3: Análisis sintáctico...");
            List<Token> tokens = analizadorLexico.getTodosLosTokens();
            analizadorSintactico = new AnalizadorSintactico(tokens);
            boolean exito = analizadorSintactico.analizar();

            // Mostrar árbol de derivación
            analizadorSintactico.imprimirArbol();

            // Paso 4: Análisis semántico
            System.out.println("🔍 Paso 4: Análisis semántico y clasificación...");
            clasificadorSemantico = new ClasificadorSemantico();
            clasificadorSemantico.analizar(analizadorLexico.getTokensValidos());

            // Paso 5: Generar reportes
            System.out.println("📊 Paso 5: Generando reportes...");
            generarReportes();

            // Paso 6: Mostrar tabla LL(1)
            System.out.println("📋 Paso 6: Tabla LL(1)...");
            analizadorSintactico.getTablaLL1().imprimirTabla();

            // Resumen final
            mostrarResumenFinal(exito);

        } catch (Exception e) {
            System.err.println("\nERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //endregion

    //region GENERACIÓN DE REPORTES

    /**
     * Genera todos los reportes requeridos.
     */
    private void generarReportes() {
        System.out.println("\n--- GENERANDO ARCHIVOS DE SALIDA ---\n");

        // 1. Reporte de errores (errores.txt)
        String reporteErrores = GeneradorReportes.generarReporteErrores(
                analizadorLexico.getTokensInvalidos(),
                analizadorSintactico.getErroresSintacticos(),
                clasificadorSemantico.getErroresSemanticos()
        );
        EscritorArchivo.escribirArchivoConMensaje("errores.txt", reporteErrores, "Reporte de errores");

        // 2. Tabla de transición (tabla_transicion.txt)
        String tablaTransicion = analizadorSintactico.getTablaLL1().generarTextoTabla();
        EscritorArchivo.escribirArchivoConMensaje("tabla_transicion.txt", tablaTransicion, "Tabla de transición");

        // 3. Árbol de derivación (arbol.dot)
        String arbolDOT = GeneradorGraphviz.generarArbolDerivacion(analizadorSintactico.getRaizArbol());
        EscritorArchivo.escribirArchivoConMensaje("arbol.dot", arbolDOT, "Árbol de derivación (DOT)");

        // 4. AST (ast.dot)
        String astDOT = GeneradorGraphviz.generarAST(analizadorSintactico.getRaizArbol());
        EscritorArchivo.escribirArchivoConMensaje("ast.dot", astDOT, "AST (DOT)");

        // 5. Reporte de clasificación
        String reporteClasificacion = GeneradorReportes.generarReporteClasificacion(clasificadorSemantico);
        EscritorArchivo.escribirArchivoConMensaje("clasificacion.txt", reporteClasificacion, "Clasificación semántica");

        System.out.println("\n✓ Todos los archivos generados exitosamente\n");
        System.out.println("Archivos generados:");
        System.out.println("  • errores.txt - Reporte de errores léxicos, sintácticos y semánticos");
        System.out.println("  • tabla_transicion.txt - Tabla LL(1) completa");
        System.out.println("  • arbol.dot - Árbol de derivación (visualizar con Graphviz)");
        System.out.println("  • ast.dot - Árbol de sintaxis abstracta (visualizar con Graphviz)");
        System.out.println("  • clasificacion.txt - Clasificación de variables, funciones, etc.\n");
    }

    /**
     * Muestra el resumen final del análisis.
     *
     * @param exitoSintactico Si el análisis sintáctico fue exitoso
     */
    private void mostrarResumenFinal(boolean exitoSintactico) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    RESUMEN FINAL                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝\n");

        int totalErrores = analizadorLexico.getTokensInvalidos().size() +
                analizadorSintactico.getErroresSintacticos().size() +
                clasificadorSemantico.getErroresSemanticos().size();

        if (totalErrores == 0 && exitoSintactico) {
            System.out.println("ANÁLISIS COMPLETADO SIN ERRORES");
            System.out.println("  El programa es léxica, sintáctica y semánticamente correcto.\n");
        } else {
            System.out.println("  xxx SE ENCONTRARON ERRORES xxx");
            System.out.println("  Total de errores: " + totalErrores);
            System.out.println("    - Errores léxicos: " + analizadorLexico.getTokensInvalidos().size());
            System.out.println("    - Errores sintácticos: " + analizadorSintactico.getErroresSintacticos().size());
            System.out.println("    - Errores semánticos: " + clasificadorSemantico.getErroresSemanticos().size());
            System.out.println("\n  Consulte el archivo errores.txt para más detalles.\n");
        }
        System.out.println("════════════════════════════════════════════════════════════\n");
    }

    //endregion



}
