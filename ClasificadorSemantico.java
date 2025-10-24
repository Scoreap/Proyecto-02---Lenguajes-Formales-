package analizadorSintactico;

import analizadorLexico.Token;
import analizadorLexico.TipoToken;
import java.util.*;

/**
 * Clasificador Semántico que identifica y categoriza elementos del código.
 * Detecta errores semánticos básicos como variables no declaradas
 *
 * @author Sophia
 */
public class ClasificadorSemantico {

    // Tablas de símbolos
    private Map<String, String> variablesDeclaradas;    // nombre -> tipo
    private Map<String, InfoFuncion> funcionesDeclaradas; // nombre -> info
    private Set<String> variablesUsadas;

    // Clasificaciones
    private Set<String> variables;
    private Set<String> funciones;
    private Set<String> operadores;
    private Set<String> simbolos;

    // Errores semánticos
    private List<String> erroresSemanticos;

    // Contexto actual
    private String funcionActual;
    private String tipoRetornoActual;

    //region CLASES AUXILIARES

    /**
     * Información de una función.
     */
    private static class InfoFuncion {
        String tipoRetorno;
        List<String> tiposParametros;
        int linea;

        InfoFuncion(String tipoRetorno, List<String> tiposParametros, int linea) {
            this.tipoRetorno = tipoRetorno;
            this.tiposParametros = tiposParametros;
            this.linea = linea;
        }
    }

    //endregion

    //region CONSTRUCTOR

    /**
     * Constructor del clasificador semántico.
     */
    public ClasificadorSemantico() {
        this.variablesDeclaradas = new HashMap<>();
        this.funcionesDeclaradas = new HashMap<>();
        this.variablesUsadas = new HashSet<>();
        this.variables = new HashSet<>();
        this.funciones = new HashSet<>();
        this.operadores = new HashSet<>();
        this.simbolos = new HashSet<>();
        this.erroresSemanticos = new ArrayList<>();
        this.funcionActual = null;
        this.tipoRetornoActual = null;
    }

    //endregion

    //region ANÁLISIS SEMÁNTICO

    /**
     * Analiza la semántica del programa a partir de los tokens.
     *
     * @param tokens Lista de tokens
     */
    public void analizar(List<Token> tokens) {
        System.out.println("\n--- INICIANDO ANÁLISIS SEMÁNTICO ---");

        // Primera pasada: recolectar declaraciones
        recolectarDeclaraciones(tokens);

        // Segunda pasada: verificar uso de variables
        verificarUsoVariables(tokens);

        // Tercera pasada: verificar returns en funciones
        verificarReturns(tokens);

        // Clasificar todos los tokens
        clasificarTokens(tokens);

        mostrarResultados();
    }

    /**
     * Recolecta todas las declaraciones de variables y funciones.
     *
     * @param tokens Lista de tokens
     */
    private void recolectarDeclaraciones(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Detectar declaración de variable: tipo identificador ;
            if (esTipo(token) && i + 2 < tokens.size()) {
                Token siguiente = tokens.get(i + 1);
                Token despuesSiguiente = tokens.get(i + 2);

                if (siguiente.getTipo() == TipoToken.IDENTIFICADOR) {
                    // Es declaración de variable si termina en ;
                    if (despuesSiguiente.getLexema().equals(";")) {
                        String nombreVar = siguiente.getLexema();
                        String tipoVar = token.getLexema();

                        if (variablesDeclaradas.containsKey(nombreVar)) {
                            erroresSemanticos.add(String.format(
                                    "ERROR SEMÁNTICO en línea %d: Variable '%s' ya fue declarada",
                                    siguiente.getLinea(), nombreVar
                            ));
                        } else {
                            variablesDeclaradas.put(nombreVar, tipoVar);
                            variables.add(nombreVar);
                        }
                    }
                    // Es declaración de función si el siguiente es (
                    else if (despuesSiguiente.getLexema().equals("(")) {
                        String nombreFunc = siguiente.getLexema();
                        String tipoRetorno = token.getLexema();

                        // Recolectar tipos de parámetros
                        List<String> tiposParametros = new ArrayList<>();
                        int j = i + 3; // Saltar tipo, nombre, (

                        while (j < tokens.size() && !tokens.get(j).getLexema().equals(")")) {
                            if (esTipo(tokens.get(j))) {
                                tiposParametros.add(tokens.get(j).getLexema());
                            }
                            j++;
                        }

                        funcionesDeclaradas.put(nombreFunc, new InfoFuncion(
                                tipoRetorno, tiposParametros, siguiente.getLinea()
                        ));
                        funciones.add(nombreFunc);

                        // Registrar parámetros como variables locales
                        j = i + 3;
                        while (j < tokens.size() && !tokens.get(j).getLexema().equals(")")) {
                            if (tokens.get(j).getTipo() == TipoToken.IDENTIFICADOR &&
                                    j > 0 && esTipo(tokens.get(j - 1))) {
                                String nombreParam = tokens.get(j).getLexema();
                                String tipoParam = tokens.get(j - 1).getLexema();
                                variablesDeclaradas.put(nombreParam, tipoParam);
                                variables.add(nombreParam);
                            }
                            j++;
                        }
                    }
                }
            }
        }
    }

    /**
     * Verifica el uso de variables (si están declaradas).
     *
     * @param tokens Lista de tokens
     */
    private void verificarUsoVariables(List<Token> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Si es un identificador usado en una asignación o expresión
            if (token.getTipo() == TipoToken.IDENTIFICADOR) {
                String nombre = token.getLexema();

                // Verificar si es uso (no declaración)
                boolean esDeclaracion = (i > 0 && esTipo(tokens.get(i - 1)));

                if (!esDeclaracion) {
                    // Verificar si es llamada a función
                    boolean esLlamadaFuncion = (i + 1 < tokens.size() &&
                            tokens.get(i + 1).getLexema().equals("("));

                    if (esLlamadaFuncion) {
                        // Verificar que la función existe
                        if (!funcionesDeclaradas.containsKey(nombre)) {
                            erroresSemanticos.add(String.format(
                                    "ERROR SEMÁNTICO en línea %d: Función '%s' no declarada",
                                    token.getLinea(), nombre
                            ));
                        }
                    } else {
                        // Es uso de variable
                        variablesUsadas.add(nombre);

                        if (!variablesDeclaradas.containsKey(nombre)) {
                            erroresSemanticos.add(String.format(
                                    "ERROR SEMÁNTICO en línea %d: Variable '%s' no declarada",
                                    token.getLinea(), nombre
                            ));
                        }
                    }
                }
            }
        }
    }

    /**
     * Verifica que los returns sean compatibles con el tipo de la función.
     *
     * @param tokens Lista de tokens
     */
    private void verificarReturns(List<Token> tokens) {
        String funcionActual = null;
        String tipoRetornoActual = null;

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            // Detectar inicio de función
            if (esTipo(token) && i + 1 < tokens.size()) {
                Token siguiente = tokens.get(i + 1);
                if (siguiente.getTipo() == TipoToken.IDENTIFICADOR &&
                        i + 2 < tokens.size() && tokens.get(i + 2).getLexema().equals("(")) {
                    funcionActual = siguiente.getLexema();
                    tipoRetornoActual = token.getLexema();
                }
            }

            // Detectar return
            if (token.esPalabraClave("return") && funcionActual != null) {
                // Verificar si tiene expresión después
                boolean tieneExpresion = false;

                if (i + 1 < tokens.size()) {
                    Token siguiente = tokens.get(i + 1);
                    if (!siguiente.getLexema().equals(";")) {
                        tieneExpresion = true;
                    }
                }

                // Validar compatibilidad
                if (tipoRetornoActual.equals("void") && tieneExpresion) {
                    erroresSemanticos.add(String.format(
                            "ERROR SEMÁNTICO en línea %d: Función 'void' no puede retornar un valor",
                            token.getLinea()
                    ));
                }

                if (!tipoRetornoActual.equals("void") && !tieneExpresion) {
                    erroresSemanticos.add(String.format(
                            "ERROR SEMÁNTICO en línea %d: Función '%s' debe retornar un valor de tipo '%s'",
                            token.getLinea(), funcionActual, tipoRetornoActual
                    ));
                }
            }

            // Detectar fin de función
            if (token.getLexema().equals("}")) {
                funcionActual = null;
                tipoRetornoActual = null;
            }
        }
    }

    /**
     * Clasifica todos los tokens en categorías.
     *
     * @param tokens Lista de tokens
     */
    private void clasificarTokens(List<Token> tokens) {
        for (Token token : tokens) {
            switch (token.getTipo()) {
                case OPERADOR_ARITMETICO, OPERADOR_RELACIONAL, ASIGNACION ->
                        operadores.add(token.getLexema());

                case PARENTESIS_IZQ, PARENTESIS_DER, LLAVE_IZQ, LLAVE_DER,
                     PUNTO_COMA, COMA ->
                        simbolos.add(token.getLexema());
            }
        }
    }

    //endregion

    //region MÉTODOS AUXILIARES

    /**
     * Verifica si un token es un tipo (int o void).
     *
     * @param token El token a verificar
     * @return true si es un tipo
     */
    private boolean esTipo(Token token) {
        return token.esPalabraClave("int") || token.esPalabraClave("void");
    }

    /**
     * Muestra los resultados del análisis semántico.
     */
    private void mostrarResultados() {
        System.out.println("\n--- RESULTADOS DEL ANÁLISIS SEMÁNTICO ---");

        System.out.println("\nClasificación de elementos:");
        System.out.println("  Variables declaradas: " + variablesDeclaradas.size());
        System.out.println("  Funciones declaradas: " + funcionesDeclaradas.size());
        System.out.println("  Operadores únicos: " + operadores.size());
        System.out.println("  Símbolos únicos: " + simbolos.size());

        if (erroresSemanticos.isEmpty()) {
            System.out.println("\n✓ Análisis semántico EXITOSO");
            System.out.println("  No se encontraron errores semánticos");
        } else {
            System.out.println("\n✗ Se encontraron " + erroresSemanticos.size() + " errores semánticos");
            System.out.println("\nErrores detectados:");
            for (int i = 0; i < erroresSemanticos.size(); i++) {
                System.out.println((i + 1) + ". " + erroresSemanticos.get(i));
            }
        }

        System.out.println("-------------------------------------------------\n");
    }

    //endregion

    //region GETTERS

    public Map<String, String> getVariablesDeclaradas() {
        return variablesDeclaradas;
    }

    public Map<String, InfoFuncion> getFuncionesDeclaradas() {
        return funcionesDeclaradas;
    }

    public Set<String> getVariables() {
        return variables;
    }

    public Set<String> getFunciones() {
        return funciones;
    }

    public Set<String> getOperadores() {
        return operadores;
    }

    public Set<String> getSimbolos() {
        return simbolos;
    }

    public List<String> getErroresSemanticos() {
        return erroresSemanticos;
    }

    //endregion
}
