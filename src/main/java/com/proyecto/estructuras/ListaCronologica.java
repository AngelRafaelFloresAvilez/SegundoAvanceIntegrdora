package com.proyecto.estructuras;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class ListaCronologica<T> {

    private final LinkedList<T> elementos = new LinkedList<>();

    public void agregar(T elemento) {
        elementos.addFirst(elemento);
    }

    public T obtenerUltimo() {
        return elementos.peekFirst();
    }

    public List<T> listar() {
        return Collections.unmodifiableList(elementos);
    }

    public List<T> filtrar(Predicate<T> condicion) {
        List<T> resultado = new ArrayList<>();
        for (T elemento : elementos) {
            if (condicion.test(elemento)) {
                resultado.add(elemento);
            }
        }
        return resultado;
    }

    public List<T> primeros(int limite) {
        List<T> resultado = new ArrayList<>();
        for (T elemento : elementos) {
            if (resultado.size() >= limite) {
                break;
            }
            resultado.add(elemento);
        }
        return resultado;
    }

    public int tamanio() {
        return elementos.size();
    }

    public boolean estaVacia() {
        return elementos.isEmpty();
    }
}
