package com.proyecto.modelo;

import java.util.ArrayList;
import java.util.List;

public class Pulsera {

    private String codigo;
    private String nombre;
    private String tipoPiedra;
    private String diseno;
    private String talla;
    private String descripcion;
    private double precioVenta;
    private int stockActual;
    private int stockMinimo;
    private List<Insumo> receta = new ArrayList<>();

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoPiedra() {
        return tipoPiedra;
    }

    public void setTipoPiedra(String tipoPiedra) {
        this.tipoPiedra = tipoPiedra;
    }

    public String getDiseno() {
        return diseno;
    }

    public void setDiseno(String diseno) {
        this.diseno = diseno;
    }

    public String getTalla() {
        return talla;
    }

    public void setTalla(String talla) {
        this.talla = talla;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public int getStockActual() {
        return stockActual;
    }

    public void setStockActual(int stockActual) {
        this.stockActual = stockActual;
    }

    public int getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public List<Insumo> getReceta() {
        return receta;
    }

    public void setReceta(List<Insumo> receta) {
        this.receta = receta;
    }

    public boolean isBajo() {
        return stockActual <= stockMinimo;
    }

    public String getEstado() {
        if (stockActual <= 0) {
            return "Agotado";
        }
        if (stockActual <= stockMinimo) {
            return "Stock bajo";
        }
        return "Disponible";
    }

    public String getClaseEstado() {
        if (stockActual <= 0) {
            return "rojo";
        }
        if (stockActual <= stockMinimo) {
            return "ambar";
        }
        return "verde";
    }

    public String getRecetaTexto() {
        StringBuilder sb = new StringBuilder();
        for (Insumo i : receta) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(i.getTexto());
        }
        return sb.toString();
    }
}
