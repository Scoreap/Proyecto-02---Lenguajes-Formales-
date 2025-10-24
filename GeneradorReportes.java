package generadores;

import analizadorLexico.Token;
import analizadorSintactico.ClasificadorSemantico;
import java.util.List;

/**
 * Genera reportes de errores léxicos y sintácticos en formato texto.
 * Crea el archivo errores.txt con información detallada de cada error.
 *
 * @author Sophia
 */
public class GeneradorReportes {

    public GeneradorReportes() {
    }

    /**
     * Genera el reporte completo de errores.
     *
     * @param tokensInvalidos Tokens léxicos inválidos
     * @param erroresSintacticos Lista de errores sintácticos
     * @param erroresSemanticos Lista de errores semánticos
     * @return Contenido del reporte
     */
    public static String generarReporteErrores(
            List<Token> tokensInvalidos,
            List<String> erroresSintacticos,
            List<String> erroresSemanticos
    ) {
        StringBuilder reporte = new StringBuilder();

        reporte.append("───────────────────────────────────────────────────────────\n");
        reporte.append("    REPORTE DE ERRORES - ANALIZADOR SINTÁCTICO\n");
        reporte.append("───────────────────────────────────────────────────────────\n\n");

        // Resumen
        int totalErrores = tokensInvalidos.size() + erroresSintacticos.size() + erroresSemanticos.size();
        reporte.append("RESUMEN:\n");
        reporte.append("  Total de errores encontrados: ").append(totalErrores).append("\n");
        reporte.append("  - Errores léxicos: ").append(tokensInvalidos.size()).append("\n");
        reporte.append("  - Errores sintácticos: ").append(erroresSintacticos.size()).append("\n");
        reporte.append("  - Errores semánticos: ").append(erroresSemanticos.size()).append("\n\n");

        // Errores léxicos
        if (!tokensInvalidos.isEmpty()) {
            reporte.append("───────────────────────────────────────────────────────\n");
            reporte.append("ERRORES LÉXICOS:\n");
            reporte.append("───────────────────────────────────────────────────────\n\n");

            for (int i = 0; i < tokensInvalidos.size(); i++) {
                Token token = tokensInvalidos.get(i);
                reporte.append(String.format("%d. %s\n", i + 1, token.formatoArchivoInvalidos()));
            }
            reporte.append("\n");
        }

        // Errores sintácticos
        if (!erroresSintacticos.isEmpty()) {
            reporte.append("───────────────────────────────────────────────────────\n");
            reporte.append("ERRORES SINTÁCTICOS:\n");
            reporte.append("───────────────────────────────────────────────────────\n\n");

            for (int i = 0; i < erroresSintacticos.size(); i++) {
                reporte.append(String.format("%d. %s\n", i + 1, erroresSintacticos.get(i)));
            }
            reporte.append("\n");
        }

        // Errores semánticos
        if (!erroresSemanticos.isEmpty()) {
            reporte.append("───────────────────────────────────────────────────────\n");
            reporte.append("ERRORES SEMÁNTICOS:\n");
            reporte.append("───────────────────────────────────────────────────────\n\n");

            for (int i = 0; i < erroresSemanticos.size(); i++) {
                reporte.append(String.format("%d. %s\n", i + 1, erroresSemanticos.get(i)));
            }
            reporte.append("\n");
        }

        // Sin errores
        if (totalErrores == 0) {
            reporte.append("───────────────────────────────────────────────────────\n");
            reporte.append("✓ NO SE ENCONTRARON ERRORES\n");
            reporte.append("  El programa es sintácticamente correcto.\n");
            reporte.append("───────────────────────────────────────────────────────\n");
        }

        reporte.append("\n───────────────────────────────────────────────────────────\n");
        reporte.append("                    FIN DEL REPORTE\n");
        reporte.append("───────────────────────────────────────────────────────────\n");

        return reporte.toString();
    }

    /**
     * Genera el reporte de clasificación semántica.
     *
     * @param clasificador El clasificador semántico
     * @return Contenido del reporte
     */
    public static String generarReporteClasificacion(ClasificadorSemantico clasificador) {
        StringBuilder reporte = new StringBuilder();

        reporte.append("───────────────────────────────────────────────────────────\n");
        reporte.append("    REPORTE DE CLASIFICACIÓN SEMÁNTICA\n");
        reporte.append("───────────────────────────────────────────────────────────\n\n");

        // Variables
        reporte.append("VARIABLES DECLARADAS (").append(clasificador.getVariables().size()).append("):\n");
        if (!clasificador.getVariables().isEmpty()) {
            for (String var : clasificador.getVariables()) {
                String tipo = clasificador.getVariablesDeclaradas().get(var);
                reporte.append(String.format("  - %s : %s\n", var, tipo != null ? tipo : "desconocido"));
            }
        } else {
            reporte.append("  (ninguna)\n");
        }
        reporte.append("\n");

        // Funciones
        reporte.append("FUNCIONES DECLARADAS (").append(clasificador.getFunciones().size()).append("):\n");
        if (!clasificador.getFunciones().isEmpty()) {
            for (String func : clasificador.getFunciones()) {
                reporte.append(String.format("  - %s()\n", func));
            }
        } else {
            reporte.append("  (ninguna)\n");
        }
        reporte.append("\n");

        // Operadores
        reporte.append("OPERADORES UTILIZADOS (").append(clasificador.getOperadores().size()).append("):\n");
        if (!clasificador.getOperadores().isEmpty()) {
            reporte.append("  ");
            for (String op : clasificador.getOperadores()) {
                reporte.append(op).append(" ");
            }
            reporte.append("\n");
        } else {
            reporte.append("  (ninguno)\n");
        }
        reporte.append("\n");

        // Símbolos
        reporte.append("SÍMBOLOS UTILIZADOS (").append(clasificador.getSimbolos().size()).append("):\n");
        if (!clasificador.getSimbolos().isEmpty()) {
            reporte.append("  ");
            for (String sim : clasificador.getSimbolos()) {
                reporte.append(sim).append(" ");
            }
            reporte.append("\n");
        } else {
            reporte.append("  (ninguno)\n");
        }
        reporte.append("\n");

        reporte.append("───────────────────────────────────────────────────────────\n");

        return reporte.toString();
    }
}
