package analizadorLexico;

/**
 * Enumeración de todos los tipos de tokens reconocidos por el analizador léxico.
 * @author Sophia
 */
public enum TipoToken {
    // Palabras clave del lenguaje
    PALABRA_CLAVE,      // class, int, void, return

    // Identificadores y literales
    IDENTIFICADOR,      // nombres de variables, funciones, clases
    NUMERO,             // literales numericos (enteros y decimales)

    // Operadores aritméticos
    OPERADOR_ARITMETICO, // +, -, *, /

    // Operadores relacionales
    OPERADOR_RELACIONAL, // <, >, ==

    // Operadores de asignación
    ASIGNACION,

    // Símbolos delimitadores
    PARENTESIS_IZQ,
    PARENTESIS_DER,
    LLAVE_IZQ,
    LLAVE_DER,

    // Símbolos de puntuación
    PUNTO_COMA,
    COMA,

    // Espacios y comentarios
    ESPACIO,            // espacios en blanco, tabs
    COMENTARIO_LINEA,   // // comentario
    COMENTARIO_BLOQUE,  // /* comentario */

    // Errores
    CARACTER_ILEGAL,
    FIN_ARCHIVO
}
