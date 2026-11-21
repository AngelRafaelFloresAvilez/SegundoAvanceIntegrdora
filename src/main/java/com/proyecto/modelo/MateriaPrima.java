package com.proyecto.modelo;

import com.proyecto.servicio.Util;

public class MateriaPrima {

    private String id;
    private String nombre;
    private String categoria;
    private double cantidadDisponible;
    private String unidadMedida;
    private double stockMinimo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(double cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public boolean isBajo() {
        return cantidadDisponible <= stockMinimo;
    }

    public String getEstado() {
        return isBajo() ? "Stock bajo" : "Normal";
    }

    public String getClaseEstado() {
        if (cantidadDisponible <= 0) {
            return "rojo";
        }
        return isBajo() ? "ambar" : "verde";
    }

    public String getCantidadTexto() {
        return Util.numero(cantidadDisponible);
    }

    public String getMinimoTexto() {
        return Util.numero(stockMinimo);
    }
}
