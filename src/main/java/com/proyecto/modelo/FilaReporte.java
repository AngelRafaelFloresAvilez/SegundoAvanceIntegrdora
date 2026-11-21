package com.proyecto.modelo;

public class FilaReporte {

    private final String etiqueta;
    private final String extra;
    private int unidades;
    private double monto;
    private double porcentaje;

    public FilaReporte(String etiqueta, String extra) {
        this.etiqueta = etiqueta;
        this.extra = extra;
    }

    public void acumular(int unidades, double monto) {
        this.unidades += unidades;
        this.monto += monto;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public String getExtra() {
        return extra;
    }

    public int getUnidades() {
        return unidades;
    }

    public double getMonto() {
        return monto;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }
}
