package analizadorLexico;

/**
 * Clase que representa un token reconocido por el analizador léxico.
 * Contiene información sobre el lexema, tipo, posición en el código fuente.
 *
 * @author Sophia
 */
public class Token {
    private String lexema;           // Texto del token
    private TipoToken tipo;          // Tipo del token
    private int linea;               // Línea donde aparece
    private int columna;             // Columna donde aparece
    private int numeroToken;         // Número secuencial del token

    //region CONSTRUCTOR

    /**
     * Constructor completo de Token.
     *
     * @param lexema El texto del token
     * @param tipo El tipo del token
     * @param linea Línea en el código fuente
     * @param columna Columna en el código fuente
     * @param numeroToken Número secuencial del token
     */
    public Token(String lexema, TipoToken tipo, int linea, int columna, int numeroToken) {
        this.lexema = lexema;
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
        this.numeroToken = numeroToken;
    }

    /**
     * Constructor simplificado sin número de token.
     */
    public Token(String lexema, TipoToken tipo, int linea, int columna) {
        this(lexema, tipo, linea, columna, 0);
    }

    //endregion

    //region GETTERS Y SETTERS

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public void setTipo(TipoToken tipo) {
        this.tipo = tipo;
    }

    public int getLinea() {
        return linea;
    }

    public void setLinea(int linea) {
        this.linea = linea;
    }

    public int getColumna() {
        return columna;
    }

    public void setColumna(int columna) {
        this.columna = columna;
    }

    public int getNumeroToken() {
        return numeroToken;
    }

    public void setNumeroToken(int numeroToken) {
        this.numeroToken = numeroToken;
    }

    //endregion

    //region MÉTODOS DE VALIDACIÓN

    /**
     * Verifica si el token es válido (no es un error).
     *
     * @return true si el token es válido
     */
    public boolean esValido() {
        return tipo != TipoToken.CARACTER_ILEGAL;
    }

    /**
     * Verifica si el token es una palabra clave específica.
     *
     * @param palabraClave La palabra clave a verificar
     * @return true si coincide
     */
    public boolean esPalabraClave(String palabraClave) {
        return tipo == TipoToken.PALABRA_CLAVE && lexema.equals(palabraClave);
    }

    /**
     * Verifica si el token es un símbolo específico.
     *
     * @param simbolo El símbolo a verificar
     * @return true si coincide
     */
    public boolean esSimbolo(String simbolo) {
        return lexema.equals(simbolo);
    }

    //endregion

    //region MÉTODOS DE FORMATO

    /**
     * Formato para archivo de tokens válidos.
     *
     * @return Cadena formateada
     */
    public String formatoArchivoValidos() {
        String tipoTexto = obtenerTextoTipo();
        if (tipo == TipoToken.ESPACIO || tipo == TipoToken.COMENTARIO_LINEA ||
                tipo == TipoToken.COMENTARIO_BLOQUE) {
            return String.format("%d - %s (Línea %d)", numeroToken, tipoTexto, linea);
        } else {
            return String.format("%d. '%s' - %s (Línea %d, Columna %d)",
                    numeroToken, lexema, tipoTexto, linea, columna);
        }
    }

    /**
     * Formato para archivo de tokens inválidos.
     *
     * @return Cadena formateada
     */
    public String formatoArchivoInvalidos() {
        return String.format("ERROR LÉXICO: '%s' - Carácter ilegal en Línea %d, Columna %d",
                lexema, linea, columna);
    }

    /**
     * Obtiene el texto descriptivo del tipo de token.
     *
     * @return Descripción del tipo
     */
    private String obtenerTextoTipo() {
        return switch (tipo) {
            case PALABRA_CLAVE -> "Palabra Clave";
            case IDENTIFICADOR -> "Identificador";
            case NUMERO -> "Número";
            case OPERADOR_ARITMETICO -> "Operador Aritmético";
            case OPERADOR_RELACIONAL -> "Operador Relacional";
            case ASIGNACION -> "Asignación";
            case PARENTESIS_IZQ -> "Paréntesis Izquierdo";
            case PARENTESIS_DER -> "Paréntesis Derecho";
            case LLAVE_IZQ -> "Llave Izquierda";
            case LLAVE_DER -> "Llave Derecha";
            case PUNTO_COMA -> "Punto y Coma";
            case COMA -> "Coma";
            case ESPACIO -> "Espacio";
            case COMENTARIO_LINEA -> "Comentario de línea";
            case COMENTARIO_BLOQUE -> "Comentario de bloque";
            case CARACTER_ILEGAL -> "Carácter Ilegal";
            case FIN_ARCHIVO -> "Fin de Archivo";
        };
    }

    //endregion

    @Override
    public String toString() {
        return String.format("Token{lexema='%s', tipo=%s, linea=%d, columna=%d}",
                lexema, tipo, linea, columna);
    }
}
