package analizadorSintactico;

import java.util.*;

/**
 * Define la gramática del lenguaje (subconjunto de Java).
 * Gramática LL(1) factorizada sin conflictos.
 *
 * @author Sophia
 */
public class Gramatica {

    // Constantes
    public static final String EPSILON = "ε";
    public static final String FIN_CADENA = "$";

    // Conjuntos de la gramática
    private Set<String> noTerminales;
    private Set<String> terminales;
    private String simboloInicial;

    // Producciones: NoTerminal -> Lista de alternativas (cada alternativa es una lista de símbolos)
    private Map<String, List<List<String>>> producciones;

    // Conjuntos PRIMERO y SIGUIENTE
    private Map<String, Set<String>> primero;
    private Map<String, Set<String>> siguiente;

    /**
     * Constructor de la gramática.
     */
    public Gramatica() {
        inicializarGramatica();
        calcularConjuntos();
    }

    /**
     * Inicializa la gramática con no terminales, terminales y producciones.
     */
    private void inicializarGramatica() {
        // No terminales (ahora incluye MiembroPrima y FactorPrima)
        noTerminales = new HashSet<>(Arrays.asList(
                "Programa", "Clase", "CuerpoClase", "Miembro", "MiembroPrima",
                "Parametros", "ListaParametros", "ListaParametrosPrima",
                "Tipo", "Bloque", "ListaSentencias", "Sentencia",
                "Asignacion", "Retorno", "RetornoPrima",
                "Expresion", "ExpresionPrima", "Termino", "TerminoPrima",
                "Factor", "FactorPrima", "Argumentos", "ArgumentosPrima"
        ));

        // Terminales
        terminales = new HashSet<>(Arrays.asList(
                "class", "int", "void", "return",
                "identificador", "numero",
                "+", "-", "*", "/", "=",
                ";", ",", "(", ")", "{", "}",
                FIN_CADENA
        ));

        // Símbolo inicial
        simboloInicial = "Programa";

        // Construir producciones
        construirProducciones();
    }

    /**
     * Define todas las producciones de la gramática.
     * GRAMÁTICA LL(1) - SIN CONFLICTOS
     */
    private void construirProducciones() {
        producciones = new HashMap<>();

        // 1. Programa → Clase
        producciones.put("Programa", Arrays.asList(
                Arrays.asList("Clase")
        ));

        // 2. Clase → class identificador { CuerpoClase }
        producciones.put("Clase", Arrays.asList(
                Arrays.asList("class", "identificador", "{", "CuerpoClase", "}")
        ));

        // 3. CuerpoClase → Miembro CuerpoClase | ε
        producciones.put("CuerpoClase", Arrays.asList(
                Arrays.asList("Miembro", "CuerpoClase"),
                Arrays.asList(EPSILON)
        ));

        // 4. Miembro → Tipo identificador MiembroPrima (FACTORIZADO - LL(1))
        producciones.put("Miembro", Arrays.asList(
                Arrays.asList("Tipo", "identificador", "MiembroPrima")
        ));

        // 5. MiembroPrima → ; | ( Parametros ) Bloque (NUEVA - RESUELVE CONFLICTO)
        producciones.put("MiembroPrima", Arrays.asList(
                Arrays.asList(";"),                                    // Declaración de variable
                Arrays.asList("(", "Parametros", ")", "Bloque")        // Declaración de función
        ));

        // 6. Parametros → ListaParametros | ε
        producciones.put("Parametros", Arrays.asList(
                Arrays.asList("ListaParametros"),
                Arrays.asList(EPSILON)
        ));

        // 7. ListaParametros → Tipo identificador ListaParametrosPrima
        producciones.put("ListaParametros", Arrays.asList(
                Arrays.asList("Tipo", "identificador", "ListaParametrosPrima")
        ));

        // 8. ListaParametrosPrima → , Tipo identificador ListaParametrosPrima | ε
        producciones.put("ListaParametrosPrima", Arrays.asList(
                Arrays.asList(",", "Tipo", "identificador", "ListaParametrosPrima"),
                Arrays.asList(EPSILON)
        ));

        // 9. Tipo → int | void
        producciones.put("Tipo", Arrays.asList(
                Arrays.asList("int"),
                Arrays.asList("void")
        ));

        // 10. Bloque → { ListaSentencias }
        producciones.put("Bloque", Arrays.asList(
                Arrays.asList("{", "ListaSentencias", "}")
        ));

        // 11. ListaSentencias → Sentencia ListaSentencias | ε
        producciones.put("ListaSentencias", Arrays.asList(
                Arrays.asList("Sentencia", "ListaSentencias"),
                Arrays.asList(EPSILON)
        ));

        // 12. Sentencia → Asignacion | Retorno | Tipo identificador ;
        producciones.put("Sentencia", Arrays.asList(
                Arrays.asList("Asignacion"),
                Arrays.asList("Retorno"),
                Arrays.asList("Tipo", "identificador", ";")  // Declaración de variable local
        ));

        // 13. Asignacion → identificador = Expresion ;
        producciones.put("Asignacion", Arrays.asList(
                Arrays.asList("identificador", "=", "Expresion", ";")
        ));

        // 14. Retorno → return RetornoPrima
        producciones.put("Retorno", Arrays.asList(
                Arrays.asList("return", "RetornoPrima")
        ));

        // 15. RetornoPrima → Expresion ; | ;
        producciones.put("RetornoPrima", Arrays.asList(
                Arrays.asList("Expresion", ";"),
                Arrays.asList(";")
        ));

        // 16. Expresion → Termino ExpresionPrima
        producciones.put("Expresion", Arrays.asList(
                Arrays.asList("Termino", "ExpresionPrima")
        ));

        // 17. ExpresionPrima → + Termino ExpresionPrima | - Termino ExpresionPrima | ε
        producciones.put("ExpresionPrima", Arrays.asList(
                Arrays.asList("+", "Termino", "ExpresionPrima"),
                Arrays.asList("-", "Termino", "ExpresionPrima"),
                Arrays.asList(EPSILON)
        ));

        // 18. Termino → Factor TerminoPrima
        producciones.put("Termino", Arrays.asList(
                Arrays.asList("Factor", "TerminoPrima")
        ));

        // 19. TerminoPrima → * Factor TerminoPrima | / Factor TerminoPrima | ε
        producciones.put("TerminoPrima", Arrays.asList(
                Arrays.asList("*", "Factor", "TerminoPrima"),
                Arrays.asList("/", "Factor", "TerminoPrima"),
                Arrays.asList(EPSILON)
        ));

        // 20. Factor → numero | identificador FactorPrima | ( Expresion )
        // FACTORIZADO - LL(1)
        producciones.put("Factor", Arrays.asList(
                Arrays.asList("numero"),
                Arrays.asList("identificador", "FactorPrima"),
                Arrays.asList("(", "Expresion", ")")
        ));

        // 21. FactorPrima → ( Argumentos ) | ε (NUEVA - RESUELVE CONFLICTO)
        producciones.put("FactorPrima", Arrays.asList(
                Arrays.asList("(", "Argumentos", ")"),  // Llamada a función
                Arrays.asList(EPSILON)                   // Variable simple
        ));

        // 22. Argumentos → Expresion ArgumentosPrima | ε
        producciones.put("Argumentos", Arrays.asList(
                Arrays.asList("Expresion", "ArgumentosPrima"),
                Arrays.asList(EPSILON)
        ));

        // 23. ArgumentosPrima → , Expresion ArgumentosPrima | ε
        producciones.put("ArgumentosPrima", Arrays.asList(
                Arrays.asList(",", "Expresion", "ArgumentosPrima"),
                Arrays.asList(EPSILON)
        ));
    }

    /**
     * Calcula los conjuntos PRIMERO y SIGUIENTE para todos los símbolos.
     */
    private void calcularConjuntos() {
        primero = new HashMap<>();
        siguiente = new HashMap<>();

        // Inicializar conjuntos vacíos
        for (String noTerminal : noTerminales) {
            primero.put(noTerminal, new HashSet<>());
            siguiente.put(noTerminal, new HashSet<>());
        }

        // Calcular PRIMERO
        boolean cambio;
        do {
            cambio = false;
            for (String noTerminal : noTerminales) {
                for (List<String> produccion : producciones.get(noTerminal)) {
                    Set<String> primeroProduccion = calcularPrimeroProduccion(produccion);
                    int tamañoAntes = primero.get(noTerminal).size();
                    primero.get(noTerminal).addAll(primeroProduccion);
                    if (primero.get(noTerminal).size() > tamañoAntes) {
                        cambio = true;
                    }
                }
            }
        } while (cambio);

        // Calcular SIGUIENTE
        siguiente.get(simboloInicial).add(FIN_CADENA);

        do {
            cambio = false;
            for (String noTerminal : noTerminales) {
                for (List<String> produccion : producciones.get(noTerminal)) {
                    for (int i = 0; i < produccion.size(); i++) {
                        String simbolo = produccion.get(i);

                        if (esNoTerminal(simbolo)) {
                            // Calcular PRIMERO de lo que sigue
                            Set<String> primeroSiguiente = new HashSet<>();
                            boolean todosContienenEpsilon = true;

                            for (int j = i + 1; j < produccion.size(); j++) {
                                Set<String> primeroSig = obtenerPrimero(produccion.get(j));
                                primeroSiguiente.addAll(primeroSig);
                                primeroSiguiente.remove(EPSILON);

                                if (!primeroSig.contains(EPSILON)) {
                                    todosContienenEpsilon = false;
                                    break;
                                }
                            }

                            int tamañoAntes = siguiente.get(simbolo).size();
                            siguiente.get(simbolo).addAll(primeroSiguiente);

                            if (todosContienenEpsilon || i == produccion.size() - 1) {
                                siguiente.get(simbolo).addAll(siguiente.get(noTerminal));
                            }

                            if (siguiente.get(simbolo).size() > tamañoAntes) {
                                cambio = true;
                            }
                        }
                    }
                }
            }
        } while (cambio);
    }

    /**
     * Calcula PRIMERO de una producción.
     */
    private Set<String> calcularPrimeroProduccion(List<String> produccion) {
        Set<String> resultado = new HashSet<>();

        for (String simbolo : produccion) {
            Set<String> primeroSimbolo = obtenerPrimero(simbolo);

            for (String s : primeroSimbolo) {
                if (!s.equals(EPSILON)) {
                    resultado.add(s);
                }
            }

            if (!primeroSimbolo.contains(EPSILON)) {
                break;
            }

            if (simbolo.equals(produccion.get(produccion.size() - 1))) {
                resultado.add(EPSILON);
            }
        }

        return resultado;
    }

    /**
     * Obtiene PRIMERO de un símbolo.
     */
    public Set<String> obtenerPrimero(String simbolo) {
        if (esTerminal(simbolo)) {
            return new HashSet<>(Arrays.asList(simbolo));
        } else if (esNoTerminal(simbolo)) {
            return primero.getOrDefault(simbolo, new HashSet<>());
        }
        return new HashSet<>();
    }

    /**
     * Obtiene SIGUIENTE de un no terminal.
     */
    public Set<String> obtenerSiguiente(String noTerminal) {
        return siguiente.getOrDefault(noTerminal, new HashSet<>());
    }

    /**
     * Verifica si un símbolo es terminal.
     */
    public boolean esTerminal(String simbolo) {
        return terminales.contains(simbolo) || simbolo.equals(EPSILON);
    }

    /**
     * Verifica si un símbolo es no terminal.
     */
    public boolean esNoTerminal(String simbolo) {
        return noTerminales.contains(simbolo);
    }

    // ═══ GETTERS ═══

    public Set<String> getNoTerminales() {
        return noTerminales;
    }

    public Set<String> getTerminales() {
        return terminales;
    }

    public String getSimboloInicial() {
        return simboloInicial;
    }

    public Map<String, List<List<String>>> getProducciones() {
        return producciones;
    }

    public Map<String, Set<String>> getPrimero() {
        return primero;
    }

    public Map<String, Set<String>> getSiguiente() {
        return siguiente;
    }
}
