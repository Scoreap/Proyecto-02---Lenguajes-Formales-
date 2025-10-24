package analizadorSintactico;

import java.util.*;

/**
 * Genera y gestiona la tabla de análisis sintáctico LL(1).
 * Detecta conflictos en la gramática.
 *
 * @author Sophia
 */
public class TablaLL1 {

    private Gramatica gramatica;

    // Tabla LL(1): [NoTerminal][Terminal] -> Producción
    private Map<String, Map<String, List<String>>> tabla;

    // Lista de conflictos detectados
    private List<String> conflictos;

    //region CONSTRUCTOR

    /**
     * Constructor de la tabla LL(1).
     *
     * @param gramatica La gramática del lenguaje
     */
    public TablaLL1(Gramatica gramatica) {
        this.gramatica = gramatica;
        this.tabla = new HashMap<>();
        this.conflictos = new ArrayList<>();

        construirTabla();
    }

    //endregion

    //region CONSTRUCCIÓN DE LA TABLA

    /**
     * Construye la tabla LL(1) a partir de la gramática.
     */
    private void construirTabla() {
        System.out.println("--- CONSTRUYENDO TABLA LL(1) ---\n");

        // Inicializar tabla vacía
        for (String noTerminal : gramatica.getNoTerminales()) {
            tabla.put(noTerminal, new HashMap<>());
        }

        // Para cada producción A → α
        for (String noTerminal : gramatica.getNoTerminales()) {
            List<List<String>> producciones = gramatica.getProducciones().get(noTerminal);

            if (producciones == null) continue;

            for (List<String> produccion : producciones) {
                // Calcular PRIMERO(α)
                Set<String> primeroAlfa = calcularPrimeroDeProduccion(produccion);

                // Para cada terminal a en PRIMERO(α), agregar A → α a M[A, a]
                for (String terminal : primeroAlfa) {
                    if (!terminal.equals(Gramatica.EPSILON)) {
                        agregarEntrada(noTerminal, terminal, produccion);
                    }
                }

                // Si ε ∈ PRIMERO(α)
                if (primeroAlfa.contains(Gramatica.EPSILON)) {
                    // Para cada terminal b en SIGUIENTE(A), agregar A → α a M[A, b]
                    Set<String> siguienteA = gramatica.obtenerSiguiente(noTerminal);
                    for (String terminal : siguienteA) {
                        agregarEntrada(noTerminal, terminal, produccion);
                    }
                }
            }
        }

        mostrarResultados();
    }

    /**
     * Agrega una entrada a la tabla, detectando conflictos.
     *
     * @param noTerminal El no terminal
     * @param terminal El terminal
     * @param produccion La producción
     */
    private void agregarEntrada(String noTerminal, String terminal, List<String> produccion) {
        Map<String, List<String>> filaNT = tabla.get(noTerminal);

        if (filaNT.containsKey(terminal)) {
            // Conflicto detectado
            String conflicto = String.format(
                    "CONFLICTO en M[%s, %s]: Ya existe %s, intentando agregar %s",
                    noTerminal, terminal, filaNT.get(terminal), produccion
            );
            conflictos.add(conflicto);
            System.out.println("ADVERTENCIA:  " + conflicto);
        } else {
            filaNT.put(terminal, new ArrayList<>(produccion));
        }
    }

    /**
     * Calcula el conjunto PRIMERO de una producción.
     *
     * @param produccion La producción
     * @return Conjunto PRIMERO
     */
    private Set<String> calcularPrimeroDeProduccion(List<String> produccion) {
        Set<String> resultado = new HashSet<>();

        for (String simbolo : produccion) {
            Set<String> primeroSimbolo = gramatica.obtenerPrimero(simbolo);

            // Agregar todos excepto epsilon
            for (String s : primeroSimbolo) {
                if (!s.equals(Gramatica.EPSILON)) {
                    resultado.add(s);
                }
            }

            // Si no contiene epsilon, detenerse
            if (!primeroSimbolo.contains(Gramatica.EPSILON)) {
                break;
            }

            // Si es el último símbolo y contiene epsilon
            if (simbolo.equals(produccion.get(produccion.size() - 1))) {
                resultado.add(Gramatica.EPSILON);
            }
        }

        return resultado;
    }

    //endregion

    //region CONSULTA DE LA TABLA

    /**
     * Obtiene la producción de la tabla para un par [NoTerminal, Terminal].
     *
     * @param noTerminal El no terminal
     * @param terminal El terminal
     * @return La producción, o null si no existe
     */
    public List<String> obtenerProduccion(String noTerminal, String terminal) {
        Map<String, List<String>> filaNT = tabla.get(noTerminal);
        if (filaNT != null) {
            return filaNT.get(terminal);
        }
        return null;
    }

    /**
     * Verifica si la gramática es LL(1).
     *
     * @return true si no hay conflictos
     */
    public boolean esLL1() {
        return conflictos.isEmpty();
    }

    //endregion

    //region GETTERS

    public Map<String, Map<String, List<String>>> getTabla() {
        return tabla;
    }

    public List<String> getConflictos() {
        return conflictos;
    }

    //endregion

    //region MÉTODOS DE VISUALIZACIÓN

    /**
     * Muestra los resultados de la construcción de la tabla.
     */
    private void mostrarResultados() {
        System.out.println("\n--- RESULTADO DE LA TABLA LL(1) ---");
        if (esLL1()) {
            System.out.println("✓ La gramática ES LL(1) - No se encontraron conflictos");
        } else {
            System.out.println("✗ La gramática NO ES LL(1) - Se encontraron " + conflictos.size() + " conflictos");
            System.out.println("\nConflictos:");
            for (String conflicto : conflictos) {
                System.out.println("  - " + conflicto);
            }
        }
        System.out.println("----------------------------------------------\n");
    }

    /**
     * Imprime la tabla LL(1) en formato legible.
     */
    public void imprimirTabla() {
        System.out.println("--- TABLA LL(1) ---\n");

        // Obtener todos los terminales usados
        Set<String> terminalesUsados = new TreeSet<>();
        for (Map<String, List<String>> fila : tabla.values()) {
            terminalesUsados.addAll(fila.keySet());
        }

        // Encabezado
        System.out.printf("%-20s | ", "No Terminal");
        for (String terminal : terminalesUsados) {
            System.out.printf("%-30s | ", terminal);
        }
        System.out.println();
        System.out.println("-".repeat(20 + terminalesUsados.size() * 33));

        // Filas
        for (String noTerminal : new TreeSet<>(gramatica.getNoTerminales())) {
            System.out.printf("%-20s | ", noTerminal);

            Map<String, List<String>> filaNT = tabla.get(noTerminal);
            for (String terminal : terminalesUsados) {
                List<String> produccion = filaNT.get(terminal);
                if (produccion != null) {
                    String prod = String.join(" ", produccion);
                    System.out.printf("%-30s | ", prod);
                } else {
                    System.out.printf("%-30s | ", "");
                }
            }
            System.out.println();
        }
        System.out.println("--------------------------\n");
    }

    /**
     * Genera la tabla en formato de texto para archivo.
     *
     * @return Cadena con la tabla
     */
    public String generarTextoTabla() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("                        TABLA DE TRANSICIÓN LL(1)\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n\n");

        if (!esLL1()) {
            sb.append("ADVERTENCIA: La gramática NO es LL(1)\n");
            sb.append("Conflictos encontrados: ").append(conflictos.size()).append("\n\n");
            for (String conflicto : conflictos) {
                sb.append("  - ").append(conflicto).append("\n");
            }
            sb.append("\n");
        } else {
            sb.append("✓ La gramática ES LL(1) - No se encontraron conflictos\n\n");
        }

        // Obtener TODOS los terminales usados en TODA la tabla
        Set<String> terminalesUsados = new TreeSet<>();
        for (Map<String, List<String>> fila : tabla.values()) {
            terminalesUsados.addAll(fila.keySet());
        }

        // Validación: Si no hay terminales, retornar tabla vacía
        if (terminalesUsados.isEmpty()) {
            sb.append("(Tabla vacía - No se encontraron entradas)\n");
            sb.append("\n═══════════════════════════════════════════════════════════════════════════════\\n");
            return sb.toString();
        }

        sb.append("NOTA: Esta tabla muestra las producciones para cada par [No Terminal, Terminal]\n\n");

        // Calcular anchos de columna
        int anchoNoTerminal = 30;
        int anchoProduccion = 45;

        // Encabezado - Mostrar TODOS los terminales
        sb.append(String.format("%-" + anchoNoTerminal + "s | ", "No Terminal"));
        for (String terminal : terminalesUsados) {
            String terminalCorto = terminal.length() > 20 ? terminal.substring(0, 17) + "..." : terminal;
            sb.append(String.format("%-" + anchoProduccion + "s | ", terminalCorto));
        }
        sb.append("\n");

        // Línea separadora
        int anchoTotal = anchoNoTerminal + (terminalesUsados.size() * (anchoProduccion + 3)) + 3;
        for (int i = 0; i < Math.min(anchoTotal, 200); i++) {
            sb.append("─");
        }
        sb.append("\n");

        // IMPORTANTE: Recorrer TODOS los no terminales de la gramática
        int filasGeneradas = 0;
        for (String noTerminal : new TreeSet<>(gramatica.getNoTerminales())) {
            sb.append(String.format("%-" + anchoNoTerminal + "s | ", noTerminal));

            Map<String, List<String>> filaNT = tabla.get(noTerminal);

            for (String terminal : terminalesUsados) {
                List<String> produccion = (filaNT != null) ? filaNT.get(terminal) : null;

                if (produccion != null) {
                    // Construir la producción en formato "A → α"
                    String prod = noTerminal + " → " + String.join(" ", produccion);

                    // Truncar si es muy largo
                    if (prod.length() > anchoProduccion) {
                        prod = prod.substring(0, anchoProduccion - 3) + "...";
                    }
                    sb.append(String.format("%-" + anchoProduccion + "s | ", prod));
                } else {
                    // Celda vacía
                    sb.append(String.format("%-" + anchoProduccion + "s | ", "—"));
                }
            }
            sb.append("\n");
            filasGeneradas++;
        }

        // Línea final
        sb.append("\n");
        for (int i = 0; i < Math.min(anchoTotal, 200); i++) {
            sb.append("═");
        }
        sb.append("\n");

        sb.append("                            FIN DE LA TABLA\n");

        for (int i = 0; i < Math.min(anchoTotal, 200); i++) {
            sb.append("═");
        }
        sb.append("\n\n");

        // Agregar Simbologia
        sb.append("Simbología:\n");
        sb.append("  → : Producción (A → α significa 'A produce α')\n");
        sb.append("  ε : Epsilon (cadena vacía)\n");
        sb.append("  $ : Fin de cadena\n");
        sb.append("  — : Sin producción para ese par [No Terminal, Terminal]\n\n");
        return sb.toString();
    }
}
