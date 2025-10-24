package generadores;

import analizadorSintactico.NodoArbol;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Genera archivos en formato DOT para visualización con Graphviz.
 * Crea representaciones visuales del árbol de derivación.
 *
 * @author Sophia
 */
public class GeneradorGraphviz {

    private static AtomicInteger contadorNodos = new AtomicInteger(0);

    public GeneradorGraphviz() {
    }

    /**
     * Genera el archivo DOT para el árbol de derivación.
     *
     * @param raiz Raíz del árbol
     * @return Contenido del archivo DOT
     */
    public static String generarArbolDerivacion(NodoArbol raiz) {
        contadorNodos.set(0);
        StringBuilder dot = new StringBuilder();

        dot.append("digraph ArbolDerivacion {\n");
        dot.append("    // Configuración del grafo\n");
        dot.append("    rankdir=TB;\n");
        dot.append("    node [shape=box, style=filled, fontname=\"Arial\"];\n");
        dot.append("    edge [fontname=\"Arial\"];\n\n");

        dot.append("    // Configuración de colores\n");
        dot.append("    node [fillcolor=\"#FFE6CC\", color=\"#FF8C00\", fontcolor=\"#000000\"];\n\n");

        dot.append("    // Nodos del árbol\n");
        if (raiz != null) {
            generarNodosDOT(raiz, dot);
        }

        dot.append("}\n");

        return dot.toString();
    }

    /**
     * Genera los nodos recursivamente en formato DOT.
     *
     * @param nodo Nodo actual
     * @param dot StringBuilder del archivo DOT
     * @return ID del nodo
     */
    private static int generarNodosDOT(NodoArbol nodo, StringBuilder dot) {
        int idNodoActual = contadorNodos.getAndIncrement();

        // Configurar apariencia según tipo de nodo
        String color = nodo.esTerminal() ? "#C5E1A5" : "#FFE6CC";
        String borderColor = nodo.esTerminal() ? "#558B2F" : "#FF8C00";
        String shape = nodo.esTerminal() ? "ellipse" : "box";

        // Crear nodo
        dot.append(String.format("    node%d [label=\"%s\", fillcolor=\"%s\", color=\"%s\", shape=%s];\n",
                idNodoActual, escaparDOT(nodo.getSimbolo()), color, borderColor, shape));

        // Generar hijos
        for (NodoArbol hijo : nodo.getHijos()) {
            int idHijo = generarNodosDOT(hijo, dot);
            dot.append(String.format("    node%d -> node%d;\n", idNodoActual, idHijo));
        }

        return idNodoActual;
    }

    /**
     * Genera el archivo DOT para el AST (versión simplificada).
     *
     * @param raiz Raíz del árbol
     * @return Contenido del archivo DOT
     */
    public static String generarAST(NodoArbol raiz) {
        contadorNodos.set(0);
        StringBuilder dot = new StringBuilder();

        dot.append("digraph AST {\n");
        dot.append("    // Configuración del grafo\n");
        dot.append("    rankdir=TB;\n");
        dot.append("    node [shape=circle, style=filled, fontname=\"Arial\"];\n");
        dot.append("    edge [fontname=\"Arial\"];\n\n");

        dot.append("    // Configuración de colores (estilo moderno)\n");
        dot.append("    node [fillcolor=\"#90CAF9\", color=\"#1976D2\", fontcolor=\"#FFFFFF\"];\n\n");

        dot.append("    // Nodos del AST (solo no terminales importantes)\n");
        if (raiz != null) {
            generarNodosASTRecursivo(raiz, dot);
        }

        dot.append("}\n");

        return dot.toString();
    }

    /**
     * Genera nodos del AST recursivamente (versión simplificada).
     *
     * @param nodo Nodo actual
     * @param dot StringBuilder del archivo DOT
     * @return ID del nodo
     */
    private static int generarNodosASTRecursivo(NodoArbol nodo, StringBuilder dot) {
        int idNodoActual = contadorNodos.getAndIncrement();

        // Solo mostrar nodos importantes en el AST
        boolean esNodoImportante = !nodo.getSimbolo().endsWith("Prima") &&
                !nodo.getSimbolo().equals("ε");

        if (esNodoImportante || nodo.esTerminal()) {
            String color = nodo.esTerminal() ? "#81C784" : "#90CAF9";
            String borderColor = nodo.esTerminal() ? "#388E3C" : "#1976D2";

            dot.append(String.format("    node%d [label=\"%s\", fillcolor=\"%s\", color=\"%s\"];\n",
                    idNodoActual, escaparDOT(nodo.getSimbolo()), color, borderColor));
        }

        // Procesar hijos
        for (NodoArbol hijo : nodo.getHijos()) {
            int idHijo = generarNodosASTRecursivo(hijo, dot);

            if (esNodoImportante || nodo.esTerminal()) {
                dot.append(String.format("    node%d -> node%d;\n", idNodoActual, idHijo));
            }
        }

        return idNodoActual;
    }

    /**
     * Escapa caracteres especiales para formato DOT.
     *
     * @param texto Texto a escapar
     * @return Texto escapado
     */
    private static String escaparDOT(String texto) {
        return texto.replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\t", "\\t");
    }
}
