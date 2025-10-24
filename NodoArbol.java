package analizadorSintactico;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un nodo en el árbol de derivación.
 * Puede ser un símbolo terminal o no terminal.
 *
 * @author Sophia
 */
public class NodoArbol {
    private String simbolo;
    private boolean esTerminal;
    private List<NodoArbol> hijos;
    private NodoArbol padre;
    private int nivel;

    //region CONSTRUCTOR

    /**
     * Constructor del nodo.
     *
     * @param simbolo El símbolo del nodo
     * @param esTerminal Indica si es terminal
     */
    public NodoArbol(String simbolo, boolean esTerminal) {
        this.simbolo = simbolo;
        this.esTerminal = esTerminal;
        this.hijos = new ArrayList<>();
        this.padre = null;
        this.nivel = 0;
    }

    //endregion

    //region MÉTODOS DE MANIPULACIÓN

    /**
     * Agrega un hijo al nodo.
     *
     * @param hijo El nodo hijo a agregar
     */
    public void agregarHijo(NodoArbol hijo) {
        hijo.setPadre(this);
        hijo.setNivel(this.nivel + 1);
        this.hijos.add(hijo);
    }

    /**
     * Verifica si el nodo es hoja (no tiene hijos).
     *
     * @return true si es hoja
     */
    public boolean esHoja() {
        return hijos.isEmpty();
    }

    //endregion

    //region GETTERS Y SETTERS

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public boolean esTerminal() {
        return esTerminal;
    }

    public void setEsTerminal(boolean esTerminal) {
        this.esTerminal = esTerminal;
    }

    public List<NodoArbol> getHijos() {
        return hijos;
    }

    public void setHijos(List<NodoArbol> hijos) {
        this.hijos = hijos;
    }

    public NodoArbol getPadre() {
        return padre;
    }

    public void setPadre(NodoArbol padre) {
        this.padre = padre;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    //endregion

    //region MÉTODOS DE VISUALIZACIÓN

    /**
     * Genera una representación en texto del árbol.
     *
     * @param prefijo Prefijo para la indentación
     * @return Cadena con el árbol
     */
    public String imprimirArbol(String prefijo) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefijo).append("└─ ").append(simbolo);

        if (esTerminal) {
            sb.append(" (Terminal)");
        }

        sb.append("\n");

        for (int i = 0; i < hijos.size(); i++) {
            NodoArbol hijo = hijos.get(i);
            String nuevoPrefijo = prefijo + (i == hijos.size() - 1 ? "   " : "│  ");
            sb.append(hijo.imprimirArbol(nuevoPrefijo));
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return simbolo + (esTerminal ? " (T)" : " (NT)");
    }

    //endregion
}
