package com.proyecto.modelo;

public class DetalleVenta {

    private final String codigoPulsera;
    private final String nombrePulsera;
    private final String tipoPiedra;
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public DetalleVenta(String codigoPulsera, String nombrePulsera, String tipoPiedra, int cantidad, double precioUnitario) {
        this.codigoPulsera = codigoPulsera;
        this.nombrePulsera = nombrePulsera;
        this.tipoPiedra = tipoPiedra;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = cantidad * precioUnitario;
    }

    public String getCodigoPulsera() {
        return codigoPulsera;
    }

    public String getNombrePulsera() {
        return nombrePulsera;
    }

    public String getTipoPiedra() {
        return tipoPiedra;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
