package com.proyecto.modelo;

import com.proyecto.servicio.Util;

public class Alerta {

    private final String tipo;
    private final String id;
    private String nombre;
    private double actual;
    private double minimo;

    public Alerta(String tipo, String id, String nombre, double actual, double minimo) {
        this.tipo = tipo;
        this.id = id;
        this.nombre = nombre;
        this.actual = actual;
        this.minimo = minimo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getActual() {
        return actual;
    }

    public double getMinimo() {
        return minimo;
    }

    public void setActual(double actual) {
        this.actual = actual;
    }

    public void setMinimo(double minimo) {
        this.minimo = minimo;
    }

    public String getActualTexto() {
        return Util.numero(actual);
    }

    public String getMinimoTexto() {
        return Util.numero(minimo);
    }

    public String getClase() {
        return actual <= 0 ? "rojo" : "ambar";
    }
}
