package analizadorLexico;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * Analizador Léxico para subconjunto de Java.
 * Reconoce palabras clave, identificadores, números, operadores, símbolos y comentarios.
 * Reporta errores léxicos con línea y columna.
 *
 * @author Sophia
 */
public class AnalizadorLexico {

    private String codigoFuente;
    private int posicion;
    private int linea;
    private int columna;
    private int numeroToken;

    private List<Token> tokensValidos;
    private List<Token> tokensInvalidos;

    // Palabras clave del lenguaje
    private static final Set<String> PALABRAS_CLAVE = new HashSet<>();

    static {
        PALABRAS_CLAVE.add("class");
        PALABRAS_CLAVE.add("int");
        PALABRAS_CLAVE.add("void");
        PALABRAS_CLAVE.add("return");
    }

    //region CONSTRUCTOR

    /**
     * Constructor del analizador léxico.
     *
     * @param codigoFuente Código fuente a analizar
     */
    public AnalizadorLexico(String codigoFuente) {
        this.codigoFuente = codigoFuente;
        this.posicion = 0;
        this.linea = 1;
        this.columna = 1;
        this.numeroToken = 0;
        this.tokensValidos = new ArrayList<>();
        this.tokensInvalidos = new ArrayList<>();
    }

    //endregion

    //region GETTERS

    public List<Token> getTokensValidos() {
        return tokensValidos;
    }

    public List<Token> getTokensInvalidos() {
        return tokensInvalidos;
    }

    public List<Token> getTodosLosTokens() {
        List<Token> todos = new ArrayList<>();
        todos.addAll(tokensValidos);
        todos.addAll(tokensInvalidos);
        return todos;
    }

    //endregion

    //region MÉTODO PRINCIPAL DE ANÁLISIS

    /**
     * Analiza el código fuente y genera la lista de tokens.
     */
    public void analizar() {
        System.out.println("--- INICIANDO ANÁLISIS LÉXICO ---");
        System.out.println("Longitud del código: " + codigoFuente.length() + " caracteres\n");

        tokensValidos.clear();
        tokensInvalidos.clear();

        while (!finDelArchivo()) {
            Token token = siguienteToken();

            if (token != null) {
                // Ignorar espacios y comentarios en el conteo
                if (token.getTipo() != TipoToken.ESPACIO &&
                        token.getTipo() != TipoToken.COMENTARIO_LINEA &&
                        token.getTipo() != TipoToken.COMENTARIO_BLOQUE) {

                    numeroToken++;
                    token.setNumeroToken(numeroToken);

                    if (token.esValido()) {
                        tokensValidos.add(token);
                        System.out.println("Válido: " + token);
                    } else {
                        tokensInvalidos.add(token);
                        System.out.println("ERROR: " + token);
                    }
                }
            }
        }

        // Agregar token de fin de archivo
        Token tokenFin = new Token("EOF", TipoToken.FIN_ARCHIVO, linea, columna, ++numeroToken);
        tokensValidos.add(tokenFin);

        mostrarResumen();
    }

    /**
     * Obtiene el siguiente token del código fuente.
     *
     * @return El siguiente token encontrado
     */
    private Token siguienteToken() {
        saltarEspacios();

        if (finDelArchivo()) {
            return null;
        }

        // Guardar posición actual para el token
        int lineaInicial = linea;
        int columnaInicial = columna;

        char caracterActual = caracterActual();

        // Comentarios de línea //
        if (caracterActual == '/' && mirarSiguiente() == '/') {
            return reconocerComentarioLinea(lineaInicial, columnaInicial);
        }

        // Comentarios de bloque /* */
        if (caracterActual == '/' && mirarSiguiente() == '*') {
            return reconocerComentarioBloque(lineaInicial, columnaInicial);
        }

        // Números
        if (Character.isDigit(caracterActual)) {
            return reconocerNumero(lineaInicial, columnaInicial);
        }

        // Identificadores y palabras clave
        if (esLetra(caracterActual)) {
            return reconocerIdentificadorOPalabraClave(lineaInicial, columnaInicial);
        }

        // Operadores relacionales (==, <, >)
        if (caracterActual == '=' && mirarSiguiente() == '=') {
            avanzar(); // primer =
            avanzar(); // segundo =
            return new Token("==", TipoToken.OPERADOR_RELACIONAL, lineaInicial, columnaInicial);
        }

        if (caracterActual == '<' || caracterActual == '>') {
            String op = String.valueOf(caracterActual);
            avanzar();
            return new Token(op, TipoToken.OPERADOR_RELACIONAL, lineaInicial, columnaInicial);
        }

        // Asignación simple
        if (caracterActual == '=') {
            avanzar();
            return new Token("=", TipoToken.ASIGNACION, lineaInicial, columnaInicial);
        }

        // Operadores aritméticos
        if (esOperadorAritmetico(caracterActual)) {
            String op = String.valueOf(caracterActual);
            avanzar();
            return new Token(op, TipoToken.OPERADOR_ARITMETICO, lineaInicial, columnaInicial);
        }

        // Símbolos de puntuación y delimitadores
        Token simbolo = reconocerSimbolo(caracterActual, lineaInicial, columnaInicial);
        if (simbolo != null) {
            avanzar();
            return simbolo;
        }

        // Carácter ilegal
        String caracterIlegal = String.valueOf(caracterActual);
        avanzar();
        return new Token(caracterIlegal, TipoToken.CARACTER_ILEGAL, lineaInicial, columnaInicial);
    }

    //endregion

    //region MÉTODOS DE RECONOCIMIENTO

    /**
     * Reconoce un número (entero o decimal).
     */
    private Token reconocerNumero(int lineaInicial, int columnaInicial) {
        StringBuilder numero = new StringBuilder();

        while (!finDelArchivo() && (Character.isDigit(caracterActual()) || caracterActual() == '.')) {
            numero.append(caracterActual());
            avanzar();
        }

        return new Token(numero.toString(), TipoToken.NUMERO, lineaInicial, columnaInicial);
    }

    /**
     * Reconoce un identificador o palabra clave.
     */
    private Token reconocerIdentificadorOPalabraClave(int lineaInicial, int columnaInicial) {
        StringBuilder lexema = new StringBuilder();

        while (!finDelArchivo() && (esLetraODigito(caracterActual()) || caracterActual() == '_')) {
            lexema.append(caracterActual());
            avanzar();
        }

        String texto = lexema.toString();
        TipoToken tipo = PALABRAS_CLAVE.contains(texto) ?
                TipoToken.PALABRA_CLAVE : TipoToken.IDENTIFICADOR;

        return new Token(texto, tipo, lineaInicial, columnaInicial);
    }

    /**
     * Reconoce un comentario de línea //.
     */
    private Token reconocerComentarioLinea(int lineaInicial, int columnaInicial) {
        StringBuilder comentario = new StringBuilder();

        // Consumir los dos //
        comentario.append(caracterActual());
        avanzar();
        comentario.append(caracterActual());
        avanzar();

        // Leer hasta fin de línea
        while (!finDelArchivo() && caracterActual() != '\n') {
            comentario.append(caracterActual());
            avanzar();
        }

        return new Token(comentario.toString(), TipoToken.COMENTARIO_LINEA, lineaInicial, columnaInicial);
    }

    /**
     * Reconoce un comentario de bloque.
     */
    private Token reconocerComentarioBloque(int lineaInicial, int columnaInicial) {
        StringBuilder comentario = new StringBuilder();

        // Consumir /*
        comentario.append(caracterActual());
        avanzar();
        comentario.append(caracterActual());
        avanzar();

        // Leer hasta encontrar */
        while (!finDelArchivo()) {
            if (caracterActual() == '*' && mirarSiguiente() == '/') {
                comentario.append(caracterActual());
                avanzar();
                comentario.append(caracterActual());
                avanzar();
                break;
            }
            comentario.append(caracterActual());
            avanzar();
        }

        return new Token(comentario.toString(), TipoToken.COMENTARIO_BLOQUE, lineaInicial, columnaInicial);
    }

    /**
     * Reconoce símbolos de puntuación y delimitadores.
     */
    private Token reconocerSimbolo(char c, int lineaInicial, int columnaInicial) {
        return switch (c) {
            case '(' -> new Token("(", TipoToken.PARENTESIS_IZQ, lineaInicial, columnaInicial);
            case ')' -> new Token(")", TipoToken.PARENTESIS_DER, lineaInicial, columnaInicial);
            case '{' -> new Token("{", TipoToken.LLAVE_IZQ, lineaInicial, columnaInicial);
            case '}' -> new Token("}", TipoToken.LLAVE_DER, lineaInicial, columnaInicial);
            case ';' -> new Token(";", TipoToken.PUNTO_COMA, lineaInicial, columnaInicial);
            case ',' -> new Token(",", TipoToken.COMA, lineaInicial, columnaInicial);
            default -> null;
        };
    }

    //endregion

    //region MÉTODOS AUXILIARES

    /**
     * Verifica si es una letra.
     */
    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    /**
     * Verifica si es letra o dígito.
     */
    private boolean esLetraODigito(char c) {
        return esLetra(c) || Character.isDigit(c);
    }

    /**
     * Verifica si es un operador aritmético.
     */
    private boolean esOperadorAritmetico(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    /**
     * Salta espacios en blanco, tabs y saltos de línea.
     */
    private void saltarEspacios() {
        while (!finDelArchivo()) {
            char c = caracterActual();
            if (c == ' ' || c == '\t' || c == '\r') {
                avanzar();
            } else if (c == '\n') {
                avanzar();
                linea++;
                columna = 1;
            } else {
                break;
            }
        }
    }

    /**
     * Avanza a la siguiente posición.
     */
    private void avanzar() {
        if (!finDelArchivo()) {
            posicion++;
            columna++;
        }
    }

    /**
     * Obtiene el carácter actual.
     */
    private char caracterActual() {
        return codigoFuente.charAt(posicion);
    }

    /**
     * Mira el siguiente carácter sin avanzar.
     */
    private char mirarSiguiente() {
        if (posicion + 1 < codigoFuente.length()) {
            return codigoFuente.charAt(posicion + 1);
        }
        return '\0';
    }

    /**
     * Verifica si se llegó al final del archivo.
     */
    private boolean finDelArchivo() {
        return posicion >= codigoFuente.length();
    }

    /**
     * Muestra resumen del análisis léxico.
     */
    private void mostrarResumen() {
        System.out.println("\n--- RESUMEN DEL ANÁLISIS LÉXICO ---");
        System.out.println("Tokens válidos: " + tokensValidos.size());
        System.out.println("Tokens inválidos (errores léxicos): " + tokensInvalidos.size());
        System.out.println("Total de tokens procesados: " + (tokensValidos.size() + tokensInvalidos.size()));
        System.out.println("-----------------------------------------------\n");
    }

    //endregion
}
