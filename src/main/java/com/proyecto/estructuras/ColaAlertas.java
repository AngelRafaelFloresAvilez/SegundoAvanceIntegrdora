package com.proyecto.estructuras;

import com.proyecto.modelo.Alerta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ColaAlertas {

    private final Queue<Alerta> cola = new LinkedList<>();

    public void encolar(Alerta nueva) {
        Alerta existente = buscar(nueva.getTipo(), nueva.getId());
        if (existente == null) {
            cola.offer(nueva);
        } else {
            existente.setNombre(nueva.getNombre());
            existente.setActual(nueva.getActual());
            existente.setMinimo(nueva.getMinimo());
        }
    }

    public Alerta verSiguiente() {
        return cola.peek();
    }

    public Alerta atender() {
        return cola.poll();
    }

    public void retirar(String tipo, String id) {
        cola.removeIf(a -> a.getTipo().equals(tipo) && a.getId().equals(id));
    }

    public List<Alerta> listar() {
        return Collections.unmodifiableList(new ArrayList<>(cola));
    }

    public int tamanio() {
        return cola.size();
    }

    public boolean estaVacia() {
        return cola.isEmpty();
    }

    private Alerta buscar(String tipo, String id) {
        for (Alerta a : cola) {
            if (a.getTipo().equals(tipo) && a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }
}
